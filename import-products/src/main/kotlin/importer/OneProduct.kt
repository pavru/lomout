package importer

import configuration.Config
import configuration.ExcelDataDto
import database.OneGroupTable
import database.OneProductTable
import org.apache.poi.ss.usermodel.Row
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

object OneProduct {
    private val logger = LoggerFactory.getLogger("import")

    fun addUpdate(): Boolean {
        logger.info("Start add/update offline product")
        val config = Config.config.excel.data
        val sheet = MyWorkbook.workbook.getSheet(config.sheet)
        var currentGroup: ResultRow? = null
        resetTouchFlag()
        for (row in sheet) {
            if (row.rowNum < config.skipRows) {
                continue
            }
            val parsedData = OneProductRow(row)
            parsedData.parse()
            if (getRowType(parsedData) == RowType.GROUP) {
                currentGroup = setCurrentGroup(parsedData, row)
                if (currentGroup == null) {
                    return false
                }
                continue
            } else if (currentGroup != null) {
                addUpdateProduct(parsedData, currentGroup)
            } else {
                logger.error("Group should be defined in excel file before any product")
                return false
            }
        }
        removeOldProducts(config)
        logger.info("Finish add/update offline products")
        return true
    }

    private fun addUpdateProduct(parsedData: OneProductRow, currentGroup: ResultRow) {
        if (validProduct(parsedData)) {
            val count = transaction { OneProductTable.select { OneProductTable.oneSku eq parsedData.oneSku!! }.count() }
            if (count == 0) {
                addProduct(parsedData, currentGroup)
            } else {
                updateProduct(parsedData, currentGroup)
            }
        }
    }

    private fun updateProduct(parsedData: OneProductRow, currentGroup: ResultRow) {
        transaction {
            OneProductTable.update({ OneProductTable.oneSku eq parsedData.oneSku!! }) {
                it[touched] = true
            }
        }
        val currentData = transaction {
            OneProductTable.select { OneProductTable.oneSku eq parsedData.oneSku!! }.first()
        }
        var theSame = true
        OneProductTable.fields.forEach { c ->
            if (c is Column && !isSkipInComparision(c, parsedData)) {
                val dbValue = currentData[c]
                val excelData = parsedData.data[utcc(c.name)]
                when {
                    dbValue != null && excelData == null -> theSame = false
                    dbValue == null && excelData != null -> theSame = false
                    dbValue == null && excelData == null -> {
                    }
                    else -> if (dbValue != excelData) theSame = false
                }
            }
        }
        if (currentGroup[OneGroupTable.id] != currentData[OneProductTable.group]) {
            theSame = false
        }
        if (!theSame) {
            transaction {
                OneProductTable.update({ OneProductTable.oneSku eq parsedData.oneSku!! }) {
                    it[OneProductTable.group] = currentGroup[OneGroupTable.id]
                    it[OneProductTable.updated] = DateTime()
                    OneProductTable.fields.forEach { c ->
                        if (c is Column && !isSkipInUpdate(c, parsedData)) {
                            val v = parsedData.data[utcc(c.name)]
                            if (v != null) {
                                @Suppress("UNCHECKED_CAST")
                                when (v) {
                                    is String -> it[c as Column<String>] = v
                                    is Int -> it[c as Column<Int>] = v
                                    is Double -> it[c as Column<Double>] = v
                                }
                            }
                        }
                    }
                }
            }
            logger.info("Product ${parsedData.oneSku}(${parsedData.russianName}) was updated in db")
        }
    }

    private fun isSkipInComparision(c: Column<out Any?>, parsedData: OneProductRow): Boolean =
        when {
            c.name == cctu(OneProductTable::group.name) -> true
            else -> !parsedData.data.containsKey(utcc(c.name))
        }

    private fun isSkipInUpdate(c: Column<out Any?>, parsedData: OneProductRow): Boolean =
        when {
            c.name == cctu(OneProductTable::group.name) -> true
            c.name == cctu(OneProductTable::oneSku.name) -> true
            else -> !parsedData.data.containsKey(utcc(c.name))
        }

    private fun addProduct(parsedData: OneProductRow, currentGroup: ResultRow) {
        transaction {
            OneProductTable.insert {
                it[OneProductTable.group] = currentGroup[OneGroupTable.id]
                it[OneProductTable.created] = DateTime()
                it[OneProductTable.updated] = DateTime()
                it[OneProductTable.touched] = true
                OneProductTable.fields.forEach { c ->
                    if (c is Column) {
                        val v = parsedData.data[utcc(c.name)]
                        if (v != null) {
                            @Suppress("UNCHECKED_CAST")
                            when (v) {
                                is String -> it[c as Column<String>] = v
                                is Int -> it[c as Column<Int>] = v
                                is Double -> it[c as Column<Double>] = v
                            }
                        }
                    }
                }
            }
        }
        logger.info("Product ${parsedData.oneSku}(${parsedData.russianName}) was added to db")
    }

    private fun validProduct(parsedData: OneProductRow): Boolean {
        if (parsedData.oneSku == null || parsedData.oneSku?.isBlank() == true) {
            logger.error("Product 1C sku is not defined, row: ${parsedData.row.rowNum + 1}")
            return false
        }
        if (parsedData.catalogSku == null || parsedData.catalogSku?.isBlank() == true) {
            logger.error("Product catalog sku is not defined, row: ${parsedData.row.rowNum + 1}")
            return false
        }
        if (parsedData.russianName == null || parsedData.russianName?.isBlank() == true) {
            logger.error("Product russian name is not defined, row: ${parsedData.row.rowNum + 1}")
        }
        return true
    }

    private fun setCurrentGroup(parsedData: OneProductRow, row: Row): ResultRow? {
        val query = OneGroupTable.select { OneGroupTable.code eq parsedData.group!! }
        return transaction {
            if (query.count() == 1) {
                query.first()
            } else {
                this@OneProduct.logger.error(
                    "Offline group ${parsedData.group} does not exits, row: ${row.rowNum + 1}"
                )
                null
            }
        }
    }

    private fun getRowType(row: OneProductRow): RowType =
        if (row.group != null && row.group?.isBlank() == false) {
            RowType.GROUP
        } else {
            RowType.PRODUCT
        }

    private fun resetTouchFlag() {
        transaction {
            OneProductTable.update { it[touched] = false }
        }
    }

    private fun removeOldProducts(config: ExcelDataDto) {
        transaction {
            OneProductTable.update({ OneProductTable.touched eq false }) {
                with(SqlExpressionBuilder) {
                    it.update(absentAge, absentAge + 1)
                }
            }
            OneProductTable.update({ OneProductTable.touched eq true }) { it[OneProductTable.absentAge] = 0 }
            OneProductTable.select { OneProductTable.touched eq false }.forEach {
                if (it[OneProductTable.absentAge] < config.maxAge) {
                    this@OneProduct.logger.info(
                        "Product one-sku:${it[OneProductTable.oneSku]} " +
                                "was marked to deleted in next imports, " +
                                "age:${it[OneProductTable.absentAge]}/${config.maxAge}"
                    )
                } else {
                    this@OneProduct.logger.info(
                        "Product one-sku:${it[OneProductTable.oneSku]} " +
                                "will be remove in this import"
                    )
                }
            }
        }
    }

    enum class RowType { GROUP, PRODUCT }
}