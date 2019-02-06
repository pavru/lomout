package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.LOADER_LOG_NAME
import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.data.Field
import net.pototskiy.apps.magemediation.api.config.data.FieldCollection
import net.pototskiy.apps.magemediation.api.config.loader.LoaderDataset
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.source.Row
import net.pototskiy.apps.magemediation.source.Sheet
import org.apache.logging.log4j.LogManager

class DatasetFields(private val sheet: Sheet, private val dataset: LoaderDataset) {

    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    private var cachedHeadersRow: Row? = null

    init {
        if (dataset.headersRow != UNDEFINED_COLUMN) {
            cachedHeadersRow = sheet[dataset.headersRow]
        }
    }

    val main: Map<Field, Attribute> by lazy {
        collectMainFields()
    }
    val extra: Map<Field, Attribute> by lazy {
        collectExtraFields()
    }
    val all: Map<Field, Attribute> by lazy {
        main.plus(extra)
    }

    private fun collectMainFields(): Map<Field, Attribute> {
        val confFields = dataset.fieldSets.find { it.mainSet }?.fields ?: FieldCollection(mapOf())
        return if (dataset.headersRow != UNDEFINED_COLUMN) {
            val fieldsFromSource = mutableMapOf<Field, Attribute>()
            val headersRow = cachedHeadersRow ?: throw LoaderStopException("There is no header row")
            headersRow.forEachIndexed { c, cell ->
                if (cell == null) {
                    throw LoaderStopException("Header cell can not be read in the position ${c+1}")
                }
                fieldsFromSource[Field(cell.stringValue, c, null, null, null)] = Attribute(
                    cell.stringValue,
                    AttributeStringType(false),
                    false,
                    false,
                    true,
                    null
                )
            }
            val unmatchedFields = confFields
                .filterNot { it.key.isNested || it.value.isSynthetic }
                .keys.map { it.name }
                .minus(fieldsFromSource.keys.map { it.name })
            if (unmatchedFields.isNotEmpty()) {
                throw LoaderStopException("Fields<${unmatchedFields.joinToString(",")}> are not in source headers")
            }
            val fieldsToUpdate = fieldsFromSource.filter { confFields.containsKey(it.key) }
            fieldsFromSource.minus(fieldsToUpdate.keys)
            confFields.forEach { entry ->
                val (field, attribute) = entry
                if (field.isNested || attribute.isSynthetic) {
                    fieldsFromSource[field] = attribute
                } else {
                    val autoField = fieldsToUpdate.keys.find { it == field }
                        ?: throw LoaderStopException("There is no filed<${field.name}> in source headers")
                    fieldsFromSource[Field(
                        field.name,
                        if (field.column == UNDEFINED_COLUMN) autoField.column else field.column,
                        field.regex,
                        field.parent,
                        field.transformer
                    )] = attribute
                }
            }
            fieldsFromSource.toMap()
        } else {
            confFields.toMap()
        }
    }

    private fun collectExtraFields(): Map<Field, Attribute> =
        dataset.fieldSets.filter { !it.mainSet }
            .map { it.fields.entries }
            .flatten()
            .associate { it.key to it.value }
}
