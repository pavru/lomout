package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.IMPORT_DATETIME
import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.excel.*
import net.pototskiy.apps.magemediation.database.VersionTable
import net.pototskiy.apps.magemediation.loader.converter.*
import net.pototskiy.apps.magemediation.loader.nested.AttributeListParser
import net.pototskiy.apps.magemediation.loader.nested.AttributeWorkbook
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet
import net.pototskiy.apps.magemediation.source.csv.CsvWorkbook
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.Duration
import org.slf4j.LoggerFactory

abstract class AbstractLoader : LoaderInterface {

    private val logger = LoggerFactory.getLogger(LOG_NAME)

    abstract val tableSet: TargetTableSet
    private lateinit var updater: DatabaseUpdater

    override fun load(sheet: Sheet, dataset: Dataset, emptyRowAction: EmptyRowAction) {
        updater = DatabaseUpdater(tableSet)
        sheet.workbook.let { if (it is CsvWorkbook) it.reset() }
        val mainHeaders = Headers(sheet, dataset).getHeaders()
        val allHeaders = mainHeaders.plus(
            dataset.fieldSets.filter { it.type != FieldSetType.MAIN }
                .flatMap { it.fields }
        )
        validateKeyFieldColumns(allHeaders)
        val generalData = mutableMapOf<String, Map<String, Any?>>()
        sheet.workbook.let { if (it is CsvWorkbook) it.reset() }
        loop@ for (row in sheet) {
            if (row.rowNum == dataset.headersRow || row.rowNum < dataset.rowsToSkip) {
                continue
            }
            if (row.countCell() == 0) {
                when(emptyRowAction){
                    EmptyRowAction.STOP -> {
                        logger.info("Stop process workbook<${row.sheet.workbook.name}> sheet<${row.sheet.name} because row<${row.rowNum}> is empty and configured action is STOP")
                        break@loop
                    }
                    EmptyRowAction.IGNORE -> {
                        logger.info("Skip process row in workbook<${row.sheet.workbook.name}> sheet<${row.sheet.name} because row<${row.rowNum}> is empty and configured action is IGNORE")
                        continue@loop
                    }
                }
            }
            try {
                val rowFiledSet = findRowFieldSet(dataset, row)
                if (rowFiledSet.type == FieldSetType.MAIN) {
                    val data = getData(row, mainHeaders)
                    generalData.forEach { _, gData ->
                        data.plus(gData)
                    }
                    validateKeyFieldData(data, allHeaders)
                    updateDatabase(data, allHeaders)
                } else {
                    val data = getData(row, rowFiledSet.fields)
                    generalData[rowFiledSet.name] = data
                }
            } catch (e: Exception) {
                logger.error(
                    "Workbook<${row.sheet.workbook.name}>, sheet<${row.sheet.name}>, row<${row.rowNum}> " +
                            "can not be processed, error: ${e.message}"
                )
                continue
            }
        }
        updateRecordAbsentAge()
        removeVeryOldRecords(dataset)
    }

    private fun validateKeyFieldColumns(allHeaders: List<Field>) {
        val keyFields = allHeaders.filter { it.keyField }
        val mainColumns = tableSet.mainTableHeaders
        if (!mainColumns.map { it.name }.containsAll(keyFields.map { it.name })) {
            throw LoaderException(
                "Table<${tableSet.entity::class.simpleName}> has no all key " +
                        "fields<${keyFields.joinToString(", ") { it.name }}>"
            )
        }
        keyFields.forEach { field ->
            val column = mainColumns.find { it.name == field.name }
            column?.let {
                if (!tableSet.isKeyFiledTypeCompatible(it, field)) {
                    throw LoaderException(
                        "Column<${column.name}> of entity<${tableSet.entity::class.simpleName}> " +
                                "type is not compatible with type of field<${field.name}>"
                    )
                }
            }
        }
    }

    private fun validateKeyFieldData(data: Map<String, Any?>, allHeaders: List<Field>) {
        val keyFields = allHeaders.filter { it.keyField }
        keyFields.forEach {
            val v = data[it.name]
            if (v == null || (v is String && v.isBlank())) {
                throw LoaderException("Field<${it.name}> is key field but has no value")
            }
        }
    }

    private fun updateDatabase(data: Map<String, Any?>, allHeaders: List<Field>) {
        updater.update(data, allHeaders)
    }

    private fun getData(row: Row, headers: List<Field>): Map<String, Any?> {
        val data: MutableMap<String, Cell> = mutableMapOf()
        headers.filter { !it.nested && it.type != FieldType.ATTRIBUTE_LIST }.forEach {
            data[it.name] = row[it.column]
        }
        headers.filter { it.nested }.forEach { header ->
            val processor = NestedAttrProcessor()
            processor.getAttrValue(header, row, headers)?.let {
                data[header.name] = it
            }
        }
        return convertValues(data.toMap(), headers)
    }

    private fun convertValues(cellValues: Map<String, Cell>, headers: List<Field>): Map<String, Any?> {
        return cellValues
            .map { value ->
                val field = headers.find { it.name == value.key } as Field
                val newValue = try {
                    convertValue(value.value, field)
                } catch (e: LoaderException) {
                    if (!field.optional || value.value.cellType != CellType.STRING || !value.value.stringValue.isBlank()) {
                        throw e
                    }
                    null
                }
                value.key to newValue
            }
            .toMap()
    }

    private fun convertValue(value: Cell, fieldDef: Field): Any = when (fieldDef.type) {
        FieldType.BOOL -> BooleanConverter(value, fieldDef).convert()
        FieldType.INT -> IntegerConverter(value, fieldDef).convert()
        FieldType.DOUBLE -> DoubleConverter(value, fieldDef).convert()
        FieldType.STRING -> StringConverter(value, fieldDef).convert()
        FieldType.TEXT -> StringConverter(value, fieldDef).convert()
        FieldType.DATE -> DateConverter(value, fieldDef).convert()
        FieldType.DATETIME -> DatetimeConverter(value, fieldDef).convert()
        FieldType.BOOL_LIST -> BooleanConverter(value, fieldDef).convertList()
        FieldType.INT_LIST -> IntegerConverter(value, fieldDef).convertList()
        FieldType.DOUBLE_LIST -> DoubleConverter(value, fieldDef).convertList()
        FieldType.STRING_LIST -> StringConverter(value, fieldDef).convertList()
        FieldType.DATE_LIST -> DateConverter(value, fieldDef).convertList()
        FieldType.DATETIME_LIST -> DatetimeConverter(value, fieldDef).convertList()
        FieldType.ATTRIBUTE_LIST ->
            throw LoaderException("Field<${fieldDef.name}> attribute list can not conveterte to any type")
    }

    private fun findRowFieldSet(dataset: Dataset, row: Row): FieldSet {
        return (if (dataset.fieldSets.count() > 1) {
            var fittedSet: FieldSet? = null
            for (set in dataset.fieldSets) {
                var fit = true
                for (field in set.fields.filter { field -> field.regex?.isNotBlank() ?: false }) {
                    val regex = Regex(field.regex ?: ".*")
                    if (!regex.matches(row[field.column].asString())) {
                        fit = false
                    }
                }
                if (fit) {
                    fittedSet = set
                    break
                }
            }
            fittedSet
        } else {
            null
        } ?: dataset.fieldSets.find { it.type == FieldSetType.MAIN }
        ?: throw LoaderException(
            "Row field set can not be found or dataset<${dataset.name}> has no main field set, " +
                    "workbook<${row.sheet.workbook.name}>, sheet<${row.sheet.name}, row<${row.rowNum}>"
        ))
    }

    inner class NestedAttrProcessor {
        fun getAttrValue(fieldDef: Field, row: Row, headers: List<Field>): Cell? {
            val parentDef = findParentDef(fieldDef, headers)
            val data = if (parentDef.nested) {
                getAttrValue(parentDef, row, headers)?.toString()
            } else {
                row[parentDef.column].stringValue
            }
            if (data == null && !fieldDef.optional) {
                throw LoaderException("Nester attribute<${fieldDef.name}> is not optional bat can not found")
            } else if (data == null) {
                return null
            }
            val workbook = AttributeWorkbook(
                AttributeListParser(data, parentDef),
                parentDef.name
            )
            val header = workbook[0][0].find { it.stringValue == fieldDef.name }
            if (header == null && !fieldDef.optional) {
                throw LoaderException("Nester attribute<${fieldDef.name}> is not optional bat can not found")
            }
            return if (header == null) null else workbook[0][1][header.address.column]
        }

        private fun findParentDef(nested: Field, headers: List<Field>): Field {
            return headers.find { it.name == nested.parent }
                ?: throw LoaderException("Can not find parent field for nested one")
        }
    }

    private fun updateRecordAbsentAge() {
        val entityClass = tableSet.entity
        transaction {
            entityClass.all().toList().forEach {
                val days = Duration(it.createdInMedium, it.updatedInMedium).standardDays
                it.absentDays = days.toInt()
            }
//            entity.all().toList().forEach { row ->
//                val days = Duration(row[entity.createdInMedium], row[entity.updatedInMedium]).standardDays
//                entity.update({ entity.id eq row[entity.id] }) {
//                    it[entity.absentDays] = days.toInt()
//                }
//            }
        }
    }

    private fun removeVeryOldRecords(dataset: Dataset) {
        val entity = tableSet.entity
        entity.table as VersionTable
        transaction {
            entity.find {
                ((entity.table.updatedInMedium less IMPORT_DATETIME)
                        and (entity.table.absentDays greaterEq dataset.maxAbsentDays))
            }
                .toList()
                .forEach {
                    it.delete()
                }
//            entityClass.select {
//                ((entityClass.updatedInMedium neq IMPORT_DATETIME)
//                        and (entityClass.absentDays greaterEq dataset.maxAbsentDays))
//            }.toList().forEach { row ->
//                entityClass.deleteWhere { entityClass.id eq row[entityClass.id] }
//            }
        }
    }

}
