package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.LOADER_LOG_NAME
import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.EmptyRowStrategy
import net.pototskiy.apps.magemediation.api.config.data.*
import net.pototskiy.apps.magemediation.api.config.loader.FieldSet
import net.pototskiy.apps.magemediation.api.config.loader.FieldSetCollection
import net.pototskiy.apps.magemediation.api.config.loader.Load
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntityClass
import net.pototskiy.apps.magemediation.database.SourceEntity
import net.pototskiy.apps.magemediation.loader.converter.*
import net.pototskiy.apps.magemediation.loader.nested.AttributeListParser
import net.pototskiy.apps.magemediation.loader.nested.AttributeWorkbook
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet
import net.pototskiy.apps.magemediation.source.csv.CsvWorkbook
import org.apache.logging.log4j.LogManager

class EntityLoader(
    private val loadConfig: Load,
    private val emptyRowStrategy: EmptyRowStrategy,
    private val sheet: Sheet
) {

    private val log = LogManager.getLogger(LOADER_LOG_NAME)

    private lateinit var updater: EntityUpdater
    private val extraData = mutableMapOf<String, Map<Field, Any>>()
    private lateinit var fieldSets: FieldSetCollection
    private lateinit var entityClass: EntityClass<PersistentSourceEntity>
    private lateinit var store: PersistentSourceEntityClass

    fun load() {
        sheet.workbook.let { if (it is CsvWorkbook) it.reset() }
        try {
            fieldSets = getFieldSets()
            entityClass = getAndUpdateEntityClass()
        } catch (e: Exception) {
            sheetException(e)
            return
        }
        store = entityClass.backend as PersistentSourceEntityClass
        updater = EntityUpdater(entityClass)
        store.resetTouchFlag(entityClass)
        sheet.workbook.let { if (it is CsvWorkbook) it.reset() }
        loop@ for (row in sheet) {
            if (row.rowNum == loadConfig.headersRow || row.rowNum < loadConfig.rowsToSkip) continue
            when (checkEmptyRow(row, emptyRowStrategy)) {
                EmptyRowTestResult.STOP -> break@loop
                EmptyRowTestResult.SKIP -> continue@loop
                EmptyRowTestResult.PROCESS -> { // process row }
                }
            }
            try {
                processRow(row)
            } catch (e: LoaderStopException) {
                rowException(row, e)
                break
            } catch (e: Exception) {
                rowException(row, e)
                continue
            }
        }
        store.markEntitiesAsRemove(entityClass)
        store.updateAbsentAge(entityClass)
        store.removeOldEntities(entityClass, loadConfig.maxAbsentDays)
    }

    private fun sheetException(e: Exception) {
        log.trace("Can not get entity class")
        log.error("{}({}:{})", e.message, sheet.workbook.name, sheet.name)
        log.trace("Exception:", e)
    }

    private fun getFieldSets(): FieldSetCollection {
        if (loadConfig.headersRow == UNDEFINED_COLUMN) {
            return loadConfig.fieldSets
        } else {
            val mainFieldSet = loadConfig.fieldSets.find { it.mainSet }
                ?: throw LoaderStopException("Load<${loadConfig.entity.name}> has no main field set")
            val definedFields = mainFieldSet.fields.toMutableMap()
            val sourceFields = sheet.sourceFields(loadConfig.headersRow, loadConfig.entity)
            sourceFields.forEach { field, _ ->
                definedFields.keys.find { it.name == field.name }?.also { oldField ->
                    val attr = definedFields[oldField]!!
                    definedFields.remove(oldField)
                    definedFields[Field(
                        oldField.name,
                        field.column,
                        oldField.regex,
                        oldField.parent,
                        oldField.transformer
                    )] = attr
                }
            }
            return FieldSetCollection(
                listOf(
                    FieldSet(
                        mainFieldSet.name,
                        mainFieldSet.mainSet,
                        FieldCollection(
                            definedFields.plus(sourceFields.minus(definedFields.keys))
                        )
                    )
                ).plus(loadConfig.fieldSets.filterNot { it.mainSet })
            )
        }
    }

    private fun getAndUpdateEntityClass(): EntityClass<PersistentSourceEntity> {
        @Suppress("UNCHECKED_CAST")
        val klass = EntityClass.getClass(loadConfig.entity.name) as? EntityClass<PersistentSourceEntity>
        return if (klass != null) {
            val newGeneratedAttrs = fieldSets.map { it.fields.values }
                .flatten()
                .filter { it.auto }
            if (newGeneratedAttrs.isNotEmpty()) {
                try {
                    klass.refineGenerateAttributes(newGeneratedAttrs)
                } catch (e: ConfigException) {
                    throw LoaderStopException(e.message)
                }
            }
            klass
        } else {
            val attributes = fieldSets.map { it.fields.values }.flatten().groupBy { it.auto }
            val definedAttribute = attributes[false] ?: emptyList()
            val generatedAttributes = attributes[true] ?: emptyList()
            EntityClass(
                loadConfig.entity.name,
                SourceEntity,
                definedAttribute,
                loadConfig.entity.open
            ).also {
                if (generatedAttributes.isNotEmpty()) {
                    try {
                        it.refineGenerateAttributes(generatedAttributes)
                    } catch (e: ConfigException) {
                        throw LoaderStopException(e.message)
                    }
                }
                EntityClass.registerClass(it)
            }
        }
    }

    private fun processRow(row: Row) {
        val rowFiledSet = findRowFieldSet(row)
        if (rowFiledSet.mainSet) {
            val data = getData(row, rowFiledSet.fields).toMutableMap()
            plusAdditionalData(data)
            validateKeyFieldData(data, rowFiledSet.fields)
            val allFields = fieldSets.map { set -> set.fields.map { it.key to it.value } }
                .flatten()
                .toMap()
            updateDatabase(data.mapKeys { allFields.getValue(it.key) })
        } else {
            val data = getData(row, rowFiledSet.fields)
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
        if (e !is LoaderException) {
            log.error("Internal error: {}", e.message)
            log.trace("Thread: {}", Thread.currentThread().name)
            log.trace("Exception: ", e)
        }
    }

    private fun plusAdditionalData(data: MutableMap<Field, Any>) =
        extraData.forEach { _, gData -> data.putAll(gData) }

    private fun checkEmptyRow(
        row: Row,
        emptyRowStrategy: EmptyRowStrategy
    ): EmptyRowTestResult {
        return if (row.countCell() == 0) {
            when (emptyRowStrategy) {
                EmptyRowStrategy.STOP -> {
                    log.info(
                        "Workbook processing is stopped according configuration({}:{}:{})",
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.STOP
                }
                EmptyRowStrategy.IGNORE -> {
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

    private fun validateKeyFieldData(data: Map<Field, Any?>, fields: Map<Field, Attribute>) {
        val keyFields = fields.filter { it.value.key }
        keyFields.forEach {
            val v = data[it.key]
            if (v == null || (v is String && v.isBlank())) {
                throw LoaderException("Field<${it.key.name}> is key field but has no value")
            }
        }
    }

    private fun updateDatabase(data: Map<Attribute, Any>) {
        updater.update(data)
    }

    private fun getData(row: Row, fields: Map<Field, Attribute>): Map<Field, Any> {
        val data: MutableMap<Field, Cell> = mutableMapOf()
        fields.filter {
            !it.key.isNested
                    && it.value.type !is AttributeAttributeListType
                    && !it.value.isSynthetic
        }.forEach {
            val cell = row[it.key.column]
                ?: if (it.value.nullable) row.getOrEmptyCell(it.key.column) else null
                    ?: throw LoaderException("There is no requested cell<${it.key.column + 1}> in row")
            testFieldRegex(it.key, cell)
            data[it.key] = cell
        }
        fields.filter { it.key.isNested }.forEach { header ->
            val processor = NestedAttrProcessor()
            processor.getAttrValue(header, row, fields)?.let {
                testFieldRegex(header.key, it)
                data[header.key] = it
            }
        }
        return convertValues(data.toMap(), fields)
    }

    private fun testFieldRegex(field: Field, cell: Cell) {
        if (!field.isMatchToPattern(cell.asString())) {
            throw LoaderException("Field<${field.name}> does not match required regular expression")
        }
    }

    private fun convertValues(
        cellValues: Map<Field, Cell>,
        headers: Map<Field, Attribute>
    ): Map<Field, Any> {
        return cellValues
            .mapNotNull { entry ->
                val (field, cell) = entry
                val attribute = headers[field]
                    ?: throw LoaderException("Can not find attribute description for field<${field.name}>")
                field.transform(cell.value)?.let { valueBeforeConversion ->
                    try {
                        convertValue(valueBeforeConversion, attribute)
                    } catch (e: LoaderException) {
                        if (!attribute.nullable || cell.cellType != CellType.STRING || !cell.stringValue.isBlank()) {
                            throw e
                        }
                        null
                    }?.let { field to it }
                }
            }
            .toMap()
    }

    private fun convertValue(value: Any, attrDesc: Attribute): Any = when (attrDesc.type) {
        is AttributeBoolType -> BooleanConverter(value, attrDesc).convert()
        is AttributeLongType -> IntegerConverter(value, attrDesc).convert()
        is AttributeDoubleType -> DoubleConverter(value, attrDesc).convert()
        is AttributeStringType -> StringConverter(value, attrDesc).convert()
        is AttributeTextType -> StringConverter(value, attrDesc).convert()
        is AttributeDateType -> DateConverter(value, attrDesc).convert()
        is AttributeDateTimeType -> DatetimeConverter(value, attrDesc).convert()
        is AttributeBoolListType -> BooleanConverter(value, attrDesc).convertList()
        is AttributeLongListType -> IntegerConverter(value, attrDesc).convertList()
        is AttributeDoubleListType -> DoubleConverter(value, attrDesc).convertList()
        is AttributeStringListType -> StringConverter(value, attrDesc).convertList()
        is AttributeDateListType -> DateConverter(value, attrDesc).convertList()
        is AttributeDateTimeListType -> DatetimeConverter(value, attrDesc).convertList()
        is AttributeAttributeListType ->
            throw LoaderException("Field<${attrDesc.name}> attribute list can not converted to any type")
    }

    private fun findRowFieldSet(row: Row): FieldSet =
        (if (fieldSets.count() > 1) {
            var fittedSet: FieldSet? = null
            for (set in fieldSets) {
                var fit = true
                for (field in set.fields.keys.filter { field -> field.regex != null }) {
                    val cell = row[field.column]
                        ?: throw LoaderException(
                            "Workbook<${row.sheet.workbook.name}>, " +
                                    "sheet<${row.sheet.name}>, " +
                                    "row<${row.rowNum + 1}> " +
                                    "does not exist, but it's required for row classification"
                        )
                    if (!field.isMatchToPattern(cell.asString())) fit = false
                }
                if (fit) {
                    fittedSet = set
                    break
                }
            }
            fittedSet
        } else {
            null
        } ?: fieldSets.find { it.mainSet }
        ?: throw LoaderException("Row field set can not be found for loading entity<${loadConfig.entity.name}>"))

    inner class NestedAttrProcessor {
        fun getAttrValue(
            field: Map.Entry<Field, Attribute>,
            row: Row,
            headers: Map<Field, Attribute>
        ): Cell? {
            val parentDef = findParentDef(field.key, headers)
            val data = if (parentDef.key.isNested) {
                getAttrValue(parentDef, row, headers)?.toString()
            } else {
                row[parentDef.key.column]?.stringValue
            }
            if (data == null && !field.value.nullable) {
                throw LoaderException("Nester attribute<${field.key.name}> is not optional bat can not found")
            } else if (data == null) {
                return null
            }
            val workbook = AttributeWorkbook(
                AttributeListParser(data, parentDef),
                parentDef.key.name
            )
            val header = workbook[0][0]?.find { it?.stringValue == field.key.name }
            if (header == null && !field.value.nullable) {
                throw LoaderException("Nester attribute<${field.key.name}> is not optional bat can not found")
            }
            return if (header == null) null else workbook[0][1]?.get(header.address.column)
        }

        private fun findParentDef(
            nested: Field,
            headers: Map<Field, Attribute>
        ): Map.Entry<Field, Attribute> {
            return headers.entries.find { it.key.name == nested.parent?.name }
                ?: throw LoaderException("Can not find parent field for nested one")
        }
    }
}
