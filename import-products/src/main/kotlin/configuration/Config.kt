package configuration

import importer.OneProductRow
import org.slf4j.LoggerFactory
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object Config {
    private var backConfig: ConfigDto? = null
    private val logger = LoggerFactory.getLogger("import")
    val config: ConfigDto
        get() {
            val v = Config.backConfig
            return if (v != null) {
                v
            } else {
                Config.backConfig = readConfig()
                if (!validateConfig(backConfig)) {
                    System.exit(1)
                }
                Config.backConfig as ConfigDto
            }
        }

    private fun validateConfig(config: ConfigDto?): Boolean {
        if (config == null) {
            logger.error("Configuration is not loaded")
            return false
        }
        if (!validateExcelFields(config)) {
            return false
        }
        if (!validateDefaultFieldSet(config)) {
            return false
        }
        return true
    }

    private fun validateDefaultFieldSet(config: ConfigDto): Boolean {
        val count = config.excel.data.fieldSets.filter { it.default }.count()
        if (count == 0) {
            logger.error("At least one field set should be declared as default")
            return false
        }
        if (count > 1) {
            logger.error("Only one field set can be declared as default")
            return false
        }
        return true
    }

    private fun validateExcelFields(config: ConfigDto): Boolean {
        val classFields = OneProductRow::class.memberProperties
            .filter { it.findAnnotation<OneProductRow.ExcelField>() != null }
            .map { it.name }
        val configFields = config.excel.data.fieldSets.flatMap { it.fields }.map { it.name }
        val commonFields = classFields.intersect(configFields)
        val wrongFields = configFields.minus(commonFields)
        val notDefinedFields = classFields.minus(commonFields)
        wrongFields.forEach {
            logger.error("Excel field $it is not allowed")
        }
        notDefinedFields.forEach {
            logger.warn("Excel field $it is not defined in configuration")
        }
        return if (wrongFields.isNotEmpty()) {
            logger.error("Allowable excel fields:\n" + classFields.joinToString(",\n"))
            false
        } else {
            true
        }
    }
}