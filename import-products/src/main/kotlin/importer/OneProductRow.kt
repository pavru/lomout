package importer

import configuration.Config
import configuration.ExcelFieldDto
import configuration.ExcelFieldSetDto
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.slf4j.LoggerFactory
import kotlin.math.floor
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class OneProductRow(val row: Row) {
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ExcelField

    private val config = Config.config.excel.data
    private val logger = LoggerFactory.getLogger("import")
    val data = mutableMapOf<String, Any?>()

    init {
        OneProductRow::class.memberProperties.forEach {
            if (it.findAnnotation<ExcelField>() != null) {
                data[it.name] = null
            }
        }
    }


    @ExcelField
    var group: String? by data
    @ExcelField
    var groupName: String? by data
    @ExcelField
    var oneSku: String? by data
    @ExcelField
    val catalogSku: String? by data
    @ExcelField
    val russianName: String? by data
    @ExcelField
    val englishName: String? by data
    @ExcelField
    val manufacturer: String? by data
    @ExcelField
    val countryOfManufacture: String? by data
    @ExcelField
    val machine: String? by data
    @ExcelField
    val machineVendor: String? by data
    @ExcelField
    val machineUnit: String? by data
    @ExcelField
    val weight: Double? by data
    @ExcelField
    val height: Double? by data
    @ExcelField
    val width: Double? by data
    @ExcelField
    val length: Double? by data

    fun parse() {
        val fieldSet = selectFieldSet()
        fieldSet.fields.forEach { def ->
            val v = cellValueAsString(def, row.getCell(def.column)) ?: ""
            val regex = Regex(def.regex ?: "^.*$")
            if (regex.matches(v)) {
                setFiledFromCell(def, row.getCell(def.column))
            } else {
                throw OneDataException("Data of field:${def.name} does not match to configured regex, row: ${row.rowNum}")
            }
        }
//        config.fields.asSequence()
//            .sortedBy { it.column }
//            .groupBy { it.column }
//            .forEach {
//                if (it.value.size > 1) {
//                    val v = cellValueAsString(it.value.first(), row.getCell(it.value.first().column)) ?: ""
//                    for (def in it.value) {
//                        val regex = Regex(
//                            def.regex
//                                ?: throw ConfigException("Field ${def.name} should have regex to distinct fields, row: ${row.rowNum}")
//                        )
//                        if (regex.matches(v)) {
//                            setFiledFromCell(def, row.getCell(def.column))
//                            break
//                        }
//                    }
//                } else {
//                    val def = it.value.first()
//                    val v = cellValueAsString(def, row.getCell(def.column)) ?: ""
//                    val regex = Regex(def.regex ?: "^.*$")
//                    if (regex.matches(v)) {
//                        setFiledFromCell(def,row.getCell(def.column))
//                    } else {
//                        throw OneDataException("Data of field:${def.name} does not match to configured regex, row: ${row.rowNum}")
//                    }
//                }
//            }
    }

    private fun selectFieldSet(): ExcelFieldSetDto {
        config.fieldSets.forEach { fieldSet ->
            val fit = fieldSet.fields.filter { it.regex != null }.all {
                val v = cellValueAsString(it, row.getCell(it.column)) ?: ""
                val regex = Regex(it.regex!!)
                regex.matches(v)
            }
            if (fit) {
                return fieldSet
            }
        }
        return config.fieldSets.find { it.default }
            ?: throw OneDataException("Excel default field set is not configured")
    }

    private fun setFiledFromCell(def: ExcelFieldDto, cell: Cell?) {
        if (cell == null || cell.cellType in arrayOf(CellType.BLANK, CellType.ERROR)) {
            data[def.name] = null
        }
        val iType = OneProductRow::class.memberProperties
            .find { it.name == def.name }?.returnType
        when (iType?.classifier) {
            String::class -> data[def.name] = cellValueAsString(def, cell)
            Int::class -> data[def.name] = cellValueAsInteger(def, cell)
            Double::class -> data[def.name] = cellValueAsDouble(def, cell)
            else -> logger.error("Unsupported filed type, field: ${def.name}, type: ${iType.toString()}, function: setFieldFromCell")
        }
    }

    private fun cellValueAsInteger(def: ExcelFieldDto, cell: Cell?): Int? {
        if (cell == null) {
            logger.error("Field ${def.name} should be integer, but excel cell is blank and will be skipped, row: ${row.rowNum}")
            return null
        }
        val cellType = if (cell.cellType == CellType.FORMULA) cell.cachedFormulaResultType else cell.cellType
        if (cellType != CellType.NUMERIC) {
            logger.error("Field ${def.name} should be integer, but excel cell contains incompatible data, row: ${row.rowNum}")
            return null
        }
        return cell.numericCellValue.toInt()
    }

    private fun cellValueAsDouble(def: ExcelFieldDto, cell: Cell?): Double? {
        if (cell == null) {
            logger.error("Field ${def.name} should be double, but excel cell is blank and will be skipped, row: ${row.rowNum}")
            return null
        }
        val cellType = if (cell.cellType == CellType.FORMULA) cell.cachedFormulaResultType else cell.cellType
        if (cellType != CellType.NUMERIC) {
            logger.error("Field ${def.name} should be double, but excel cell contains incompatible data, row: ${row.rowNum}")
            return null
        }
        return cell.numericCellValue
    }

    private fun cellValueAsString(def: ExcelFieldDto, cell: Cell?): String? {
        if (cell == null) {
            logger.error("Field ${def.name} should be string, but excel cell is blank and will be skipped, row: ${row.rowNum}")
            return null
        }
        val cellType = if (cell.cellType == CellType.FORMULA) cell.cachedFormulaResultType else cell.cellType
        return when (cellType) {
            CellType.BLANK -> {
                logger.error("Field ${def.name} should be string, but excel cell is blank and will be skipped, row: ${row.rowNum}")
                null
            }
            CellType.NUMERIC -> doubleToString(cell.numericCellValue)
            CellType.STRING -> cell.stringCellValue
            CellType.BOOLEAN -> if (cell.booleanCellValue) "true" else "false"
            else -> {
                logger.error("Field ${def.name} should be string, but excel cell contains incompatible data, row: ${row.rowNum}")
                null
            }
        }
    }

    private fun doubleToString(v: Double): String {
        return if ((v - floor(v)) == 0.0) {
            v.toBigDecimal().toBigInteger().toString()
        } else {
            v.toString()
        }
    }
}