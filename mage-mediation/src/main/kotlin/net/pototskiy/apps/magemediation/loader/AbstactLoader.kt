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
        val allHeaders = getAllHeadersList(mainHeaders, dataset)
        validateKeyFieldColumns(allHeaders)
        val generalData = mutableMapOf<String, Map<String, Any?>>()
        sheet.workbook.let { if (it is CsvWorkbook) it.reset() }
        loop@ for (row in sheet) {
            if (row.rowNum == dataset.headersRow || row.rowNum < dataset.rowsToSkip) {
                continue
            }
            when (checkEmptyRow(row, emptyRowAction)) {
                EmptyRowTestResult.STOP -> break@loop
                EmptyRowTestResult.SKIP -> continue@loop
                EmptyRowTestResult.PROCESS -> { // process row }
                }
            }
            try {
                processRow(dataset, row, mainHeaders, generalData, allHeaders)
            } catch (e: Exception) {
                rowException(row, e)
                continue
            }
        }
        updateRecordAbsentAge()
        removeVeryOldRecords(dataset)
    }

    private fun processRow(
        dataset: Dataset,
        row: Row,
        mainHeaders: List<Field>,
        generalData: MutableMap<String, Map<String, Any?>>,
        allHeaders: List<Field>
    ) {
        val rowFiledSet = findRowFieldSet(dataset, row)
        if (rowFiledSet.type == FieldSetType.MAIN) {
            val data = getData(row, mainHeaders).toMutableMap()
            plusAdditionalData(data, generalData)
            validateKeyFieldData(data, allHeaders)
            updateDatabase(data, allHeaders)
        } else {
            val data = getData(row, rowFiledSet.fields)
            generalData[rowFiledSet.name] = data
        }
    }

    private fun getAllHeadersList(
        mainHeaders: List<Field>,
        dataset: Dataset
    ): List<Field> {
        return mainHeaders.plus(
            dataset.fieldSets.filter { it.type != FieldSetType.MAIN }
                .flatMap { it.fields }
        )
    }

    private fun rowException(row: Row, e: Exception) {
        logger.error(
            "Workbook<${row.sheet.workbook.name}>, sheet<${row.sheet.name}>, row<${row.rowNum + 1}> " +
                    "can not be processed, error: ${e.message}"
        )
        if (e !is LoaderException) {
            logger.error("Internal error", e)
        }
    }

    private fun plusAdditionalData(
        data: MutableMap<String, Any?>,
        generalData: MutableMap<String, Map<String, Any?>>
    ) {
        generalData.forEach { _, gData ->
            data.putAll(gData)
        }
    }

    private fun checkEmptyRow(
        row: Row,
        emptyRowAction: EmptyRowAction
    ): EmptyRowTestResult {
        return if (row.countCell() == 0) {
            when (emptyRowAction) {
                EmptyRowAction.STOP -> {
                    logger.info("Stop process workbook<${row.sheet.workbook.name}> sheet<${row.sheet.name} because row<${row.rowNum + 1}> is empty and configured action is STOP")
                    EmptyRowTestResult.STOP
                }
                EmptyRowAction.IGNORE -> {
                    logger.info("Skip process row in workbook<${row.sheet.workbook.name}> sheet<${row.sheet.name} because row<${row.rowNum + 1}> is empty and configured action is IGNORE")
                    EmptyRowTestResult.SKIP
                }
            }
        } else {
            EmptyRowTestResult.PROCESS
        }
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
            val cell = row[it.column]
                ?: throw LoaderException("There is no requested cell<${it.column + 1}> in row")
            testFieldRegex(it, cell)
            data[it.name] = cell
        }
        headers.filter { it.nested }.forEach { header ->
            val processor = NestedAttrProcessor()
            processor.getAttrValue(header, row, headers)?.let {
                testFieldRegex(header, it)
                data[header.name] = it
            }
        }
        return convertValues(data.toMap(), headers)
    }

    private fun testFieldRegex(fieldDef: Field, cell: Cell) {
        fieldDef.regex?.let {
            val regex = Regex(it)
            if (!regex.matches(cell.asString())) {
                throw LoaderException("Field<${fieldDef.name}> does not match required regular expression")
            }
        }
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
            throw LoaderException("Field<${fieldDef.name}> attribute list can not converted to any type")
    }

    private fun findRowFieldSet(dataset: Dataset, row: Row): FieldSet {
        return (if (dataset.fieldSets.count() > 1) {
            var fittedSet: FieldSet? = null
            for (set in dataset.fieldSets) {
                var fit = true
                for (field in set.fields.filter { field -> field.regex?.isNotBlank() ?: false }) {
                    val cell = row[field.column]
                        ?: throw LoaderException(
                            "Workbook<${row.sheet.workbook.name}>, " +
                                    "sheet<${row.sheet.name}>, " +
                                    "row<${row.rowNum + 1}> " +
                                    "does not exist, but it's required for roq classification"
                        )
                    val regex = Regex(field.regex ?: ".*")
                    if (!regex.matches(cell.asString())) {
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
        }
            ?: dataset.fieldSets.find { it.type == FieldSetType.MAIN }
            ?: throw LoaderException(
                "Row field set can not be found or dataset<${dataset.name}> has no main field set, " +
                        "workbook<${row.sheet.workbook.name}>, sheet<${row.sheet.name}, row<${row.rowNum + 1}>"
            ))
    }

    inner class NestedAttrProcessor {
        fun getAttrValue(fieldDef: Field, row: Row, headers: List<Field>): Cell? {
            val parentDef = findParentDef(fieldDef, headers)
            val data = if (parentDef.nested) {
                getAttrValue(parentDef, row, headers)?.toString()
            } else {
                row[parentDef.column]?.stringValue
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
            val header = workbook[0][0]?.find { it.stringValue == fieldDef.name }
            if (header == null && !fieldDef.optional) {
                throw LoaderException("Nester attribute<${fieldDef.name}> is not optional bat can not found")
            }
            return if (header == null) null else workbook[0][1]?.get(header.address.column)
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
        }
    }

}
