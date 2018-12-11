package importer

import configuration.Config
import configuration.ConfigDto
import database.OneGroupEntity
import database.OneGroupTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
object OneGroup {
    private val logger = LoggerFactory.getLogger("import")

    fun addUpdate() {
        logger.info("Start add/update offline groups")
        val config = Config.config
        val gSheet = MyWorkbook.workbook.getSheet(config.excel.group.sheet)
        resetTouchFlag()
        for (eRow in gSheet) {
            if (eRow.rowNum < config.excel.group.skipRows) {
                continue
            }
            val code = eRow.getCell(config.excel.group.code.column)?.stringCellValue
            val name = eRow.getCell(config.excel.group.name.column)?.stringCellValue
            if (validCode(code, eRow.rowNum)) {
                code as String
                if (validName(name, eRow.rowNum)) {
                    name as String
                    val count = transaction { OneGroupTable.select { OneGroupTable.code eq code }.count() }
                    when {
                        count == 1 -> updateGroup(code, name)
                        count == 0 -> createNewGroup(code, name)
                        count > 1 -> logger.error("Database is corrupted, more than 2 groups with code $code are in db")
                    }
                } else {
                    println("Error: group $code has no name")
                }
            }
        }
        removeOldGroups(config)
        logger.info("Finish add/update offline groups")
    }

    private fun createNewGroup(code: String, name: String) {
        transaction {
            OneGroupEntity.new {
                this.code = code
                this.name = name
                this.created = DateTime()
                this.updated = DateTime()
                this.touched = true
            }
        }
        logger.info("Offline group code:$code with name:$name was created")
    }

    private fun updateGroup(code: String, name: String) {
        val v = transaction {
            OneGroupTable
                .slice(OneGroupTable.name)
                .select { OneGroupTable.code eq code }
                .first()
        }
        if (v[OneGroupTable.name] != name) {
            transaction {
                OneGroupTable.update({ OneGroupTable.code eq code }) {
                    it[OneGroupTable.name] = name
                    it[updated] = DateTime()
                }
            }
        }
        transaction {
            OneGroupTable.update({ OneGroupTable.code eq code }) {
                it[touched] = true
            }
        }
    }

    private fun removeOldGroups(config: ConfigDto) {
        transaction {
            OneGroupTable.update({ OneGroupTable.touched eq false }) {
                with(SqlExpressionBuilder) {
                    it.update(absentAge, absentAge + 1)
                }
            }
            OneGroupTable.update({ OneGroupTable.touched eq true }) { it[OneGroupTable.absentAge] = 0 }
            OneGroupTable.select { OneGroupTable.touched eq false }.forEach {
                if (it[OneGroupTable.absentAge] < config.excel.group.maxAge) {
                    this@OneGroup.logger.info(
                        "Group code:${it[OneGroupTable.code]} " +
                                "with name:${it[OneGroupTable.name]} " +
                                "was marked to deleted in next imports, " +
                                "age:${it[OneGroupTable.absentAge]}/${config.excel.group.maxAge}"
                    )
                } else {
                    this@OneGroup.logger.info(
                        "Group code:${it[OneGroupTable.code]} with name:${it[OneGroupTable.name]} " +
                                "will be remove in this import"
                    )
                }
            }
            OneGroupTable.deleteWhere { OneGroupTable.absentAge greaterEq config.excel.group.maxAge }
        }
    }

    private fun resetTouchFlag() {
        transaction {
            // reset touch flag
            OneGroupTable.update { it[touched] = false }
        }
    }

    private fun validCode(code: String?, row: Int): Boolean {
        if (code == null) {
            logger.warn("Offline group code is null, row: ${row + 1}, and skipped")
            return false
        }
        val regex = Regex(Config.config.excel.group.code.regex ?: "^.*$")
        if (regex.matches(code)) return true
        logger.warn(
            "Offline group code $code does not match configured regex, row: ${row + 1}, and skipped "
        )
        return false
    }

    private fun validName(name: String?, row: Int): Boolean {
        if (name == null) {
            logger.error("Group name is not defined in row ${row + 1}, and skipped")
            return false
        }
        val regex = Regex(Config.config.excel.group.name.regex ?: "^.*$")
        if (regex.matches(name)) return true
        logger.error("Group name does not match configured regex, row: ${row + 1}, and skipped")
        return false
    }
}