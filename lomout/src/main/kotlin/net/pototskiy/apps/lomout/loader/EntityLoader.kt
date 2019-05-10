package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.AppAttributeException
import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.AppRowException
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.config.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.config.loader.FieldSet
import net.pototskiy.apps.lomout.api.config.loader.Load
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.FieldAttributeMap
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import org.apache.logging.log4j.LogManager
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class EntityLoader(
    private val loadConfig: Load,
    private val emptyRowBehavior: EmptyRowBehavior,
    private val sheet: Sheet
) {

    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    var processedRows = 0L

    private lateinit var updater: EntityUpdater
    private val extraData = mutableMapOf<String, Map<AnyTypeAttribute, Type?>>()
    private var fieldSets = loadConfig.fieldSets
    private var eType = loadConfig.entity

    fun load() {
        updater = EntityUpdater(eType)
        DbEntity.resetTouchFlag(eType)
        processRows()
        DbEntity.markEntitiesAsRemove(eType)
        DbEntity.updateAbsentAge(eType)
        DbEntity.removeOldEntities(eType, loadConfig.maxAbsentDays)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun processRows() {
        loop@ for (row in sheet) {
            processedRows++
            if (row.rowNum == loadConfig.headersRow || row.rowNum < loadConfig.rowsToSkip) continue
            when (checkEmptyRow(row, emptyRowBehavior)) {
                EmptyRowTestResult.STOP -> break@loop
                EmptyRowTestResult.SKIP -> continue@loop
                EmptyRowTestResult.PROCESS -> { // assemble row }
                }
            }
            try {
                processRow(row)
            } catch (e: AppRowException) {
                rowException(row, e)
                continue
            } catch (e: Exception) {
                rowException(row, e)
                continue
            }
        }
    }

    private fun processRow(row: Row) {
        val rowFiledSet = findRowFieldSet(row)
        if (rowFiledSet.mainSet) {
            val data = getData(row, rowFiledSet.fieldToAttr).toMutableMap()
            plusAdditionalData(data)
            validateKeyFieldData(data, rowFiledSet.fieldToAttr)
            @Suppress("UNCHECKED_CAST")
            updater.update(data)
        } else {
            val data = getData(row, rowFiledSet.fieldToAttr)
            extraData[rowFiledSet.name] = data
        }
    }

    private fun rowException(row: Row, e: Exception) {
        log.error(
            "{}({}:{}:{})",
            e.message,
            row.sheet.workbook.name,
            row.sheet.name,
            row.rowNum + 1
        )
        if (e !is AppException) {
            log.error("Internal error: {}", e.message)
            log.trace("Thread: {}", Thread.currentThread().name)
            log.trace("Exception: ", e)
        }
    }

    private fun plusAdditionalData(data: MutableMap<AnyTypeAttribute, Type?>) =
        extraData.forEach { (_, gData) -> data.putAll(gData) }

    private fun checkEmptyRow(
        row: Row,
        emptyRowBehavior: EmptyRowBehavior
    ): EmptyRowTestResult {
        return if (row.countCell() == 0 || row.all { it == null || it.cellType == CellType.BLANK }) {
            when (emptyRowBehavior) {
                EmptyRowBehavior.STOP -> {
                    log.info(
                        "Workbook processing is stopped according configuration({}:{}:{})",
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.STOP
                }
                EmptyRowBehavior.IGNORE -> {
                    log.info(
                        "Empty row is skipped according configuration({}:{}:{})",
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.SKIP
                }
            }
        } else {
            EmptyRowTestResult.PROCESS
        }
    }

    private fun validateKeyFieldData(
        data: Map<AnyTypeAttribute, Type?>,
        fields: FieldAttributeMap
    ) {
        val keyFields = fields.filter { it.value.key }
        keyFields.forEach { (_, attr) ->
            val v = data[attr]
            if (v == null || (v is StringType && v.value.isBlank())) {
                throw AppAttributeException("Attribute<${attr.name}> is key but has no value")
            }
        }
    }

    @Suppress("ComplexMethod", "ThrowsCount")
    private fun getData(row: Row, fields: FieldAttributeMap): Map<AnyTypeAttribute, Type?> {
        val data: MutableMap<AnyTypeAttribute, Type?> = mutableMapOf()

        fun readNestedField(
            field: Field,
            attribute: Attribute<out Type>
        ) {
            val parentAttr = fields[field.parent]
            if ((data[parentAttr] as AttributeListType).containsKey(attribute.name) &&
                field.parent?.parent != null
            ) {
                readNestedField(field.parent!!, fields[field.parent!!]!!)
            }
            val attrCell = (data[parentAttr] as AttributeListType).value[attribute.name]
            if (attrCell == null && !attribute.nullable && attribute.valueType != AttributeListType::class) {
                throw AppCellDataException("Attribute<${attribute.name}> is not nullable and there is no data for it")
            } else if (attrCell == null) {
                data[attribute] = null
                return
            }
            @Suppress("UNCHECKED_CAST")
            val v = (attribute.reader as AttributeReader<Type>).read(attribute, attrCell)
            data[attribute] = v
        }

        fields.filterNot { it.key.isNested || it.value.isSynthetic }.forEach { (field, attr) ->
            val cell = row[field.column]
                ?: if (attr.nullable) row.getOrEmptyCell(field.column) else null
                    ?: throw AppRowException("There is no requested cell<${field.column + 1}> in row")
            testFieldRegex(field, cell)
            @Suppress("UNCHECKED_CAST")
            data[attr] = (attr.reader as AttributeReader<Type>).read(attr, cell).also {
                if (it == null && (!attr.nullable || attr.key)) {
                    throw AppCellDataException("Attribute<${attr.name}> is not nullable and can not be null")
                }
            }
        }
        fields.filter { it.key.isNested }.forEach { (field, attr) ->
            readNestedField(field, attr)
        }
        return data
    }

    private fun testFieldRegex(field: Field, cell: Cell) {
        if (!field.isMatchToPattern(cell.asString())) {
            throw AppCellDataException("Field<${field.name}> does not match required regular expression")
        }
    }

    private fun findRowFieldSet(row: Row): FieldSet =
        (if (fieldSets.count() > 1) {
            var fittedSet: FieldSet? = null
            for (set in fieldSets) {
                if (testRowAgainstFieldSet(row, set)) {
                    fittedSet = set
                    break
                }
            }
            fittedSet
        } else {
            null
        } ?: fieldSets.mainSet)

    private fun testRowAgainstFieldSet(row: Row, set: FieldSet): Boolean {
        var fit = true
        for (field in set.fields.filter { field -> field.regex != null }) {
            val cell = row[field.column]
                ?: throw AppRowException(
                    "Workbook<${row.sheet.workbook.name}>, " +
                            "sheet<${row.sheet.name}>, " +
                            "row<${row.rowNum + 1}> " +
                            "does not exist, but it's required for row classification"
                )
            if (!field.isMatchToPattern(cell.asString())) fit = false
        }
        return fit
    }
}
