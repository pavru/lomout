package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.UNDEFINED_COLUMN
import net.pototskiy.apps.lomout.api.UNDEFINED_ROW
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.documentMetadata
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.FieldAttributeMap
import net.pototskiy.apps.lomout.api.source.FieldCollection
import net.pototskiy.apps.lomout.api.source.readFieldNamesFromSource
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.collections.set
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Field set
 *
 * @property name The set name
 * @property mainSet The flag of main set
 * @property fieldToAttr The field to attribute map
 * @property fields Fields collection
 * @constructor
 */
data class FieldSet(
    val name: String,
    val mainSet: Boolean,
    val fieldToAttr: FieldAttributeMap
) {
    val fields: FieldCollection = fieldToAttr.fields

    /**
     * Field set builder class
     *
     * @property helper The config build helper
     * @property entityType The entity type name
     * @property name The field set name
     * @property mainSet The flag of main set
     * @property withSourceHeaders The flag that sources has headers
     * @property sources The sources
     * @property headerRow The header row number
     * @property fields The field to attribute map
     * @property lastFieldName The last defined field name, **do not use in DSL**
     * @property lastField The last defined field
     * @constructor
     */
    @Suppress("TooManyFunctions")
    @ConfigDsl
    class Builder(
        val helper: ConfigBuildHelper,
        private val entityType: KClass<out Document>,
        private val name: String,
        private val mainSet: Boolean = false,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?,
        private val toAttribute: Boolean
    ) {
        private var fields = mutableMapOf<Field, DocumentMetadata.Attribute>()
        private var lastFieldName: String? = null
        private var lastField: Field? = null

        /**
         * Define a field.
         *
         * Fields of source without headers must have column definition.
         * Pattern to validate field value and determinate, which field set must be used for row.
         *
         * ```
         * ...
         *  field("name") {
         *      column(2)
         *      pattern(".*")
         *      pattern(Regex(".*"))
         *  }
         * ...
         * ```
         *
         * @param name The field name
         * @param block The field definition
         * @return Field
         */
        @ConfigDsl
        fun field(name: String, block: Field.Builder.() -> Unit = {}): Field {
            if (lastField != null) {
                addFiled(lastField!!, null)
            }
            this.lastFieldName = name
            return Field.Builder(name).apply(block).build().also {
                lastField = it
            }
        }

        private fun addFiled(field: Field, lastAttribute: DocumentMetadata.Attribute?) {
            if (fields.containsKey(field)) {
                throw AppConfigException(badPlace(field), "Field '${field.name}' is already defined.")
            }
            @Suppress("UNCHECKED_CAST")
            fields[field] = lastAttribute
                ?: entityType.documentMetadata.attributes.values.find { it.fieldName == field.name }
                        ?: throw AppConfigException(
                    unknownPlace(), "Entity type '${entityType.qualifiedName}' " +
                            "has no attribute for the '${field.name}'"
                )
            this.lastField = null
        }

        /**
         * Reference to attribute that is used for the field
         *
         * ```
         * ...
         *  field("name") to attribute("name")
         * ...
         * ```
         *
         * @param name The attribute name
         * @return AttributeWithName
         */
        @ConfigDsl
        fun attribute(name: String) = AttributeWithName(name)

        /**
         * Operation to map field to attribute
         *
         * @receiver Field
         * @param attribute The field related attribute
         */
        @ConfigDsl
        infix fun Field.to(attribute: DocumentMetadata.Attribute) {
            if (!toAttribute) {
                throw AppConfigException(unknownPlace(), "Use 'from' mapping operator.")
            }
            addFiled(this, attribute)
        }

        /**
         * Operation to map field to attribute
         *
         * @receiver The field
         * @param attribute The attribute
         */
        infix fun Field.to(attribute: KProperty1<out Document, *>) {
            if (!toAttribute) {
                throw AppConfigException(unknownPlace(), "Use 'from' mapping operator.")
            }
            val attr = entityType.documentMetadata.attributes[attribute.name]
                ?: throw AppConfigException(
                    unknownPlace(),
                    "Attribute '%s' is not defined.".format(attribute.name)
                )
            addFiled(this, attr)
        }

        /**
         * Operation to map field to attribute
         *
         * @receiver Field
         * @param attribute AttributeWithName
         */
        @ConfigDsl
        infix fun Field.to(attribute: AttributeWithName) {
            if (!toAttribute) {
                throw AppConfigException(unknownPlace(), "Use 'from' mapping operator.")
            }
            val attr = entityType.documentMetadata.attributes[attribute.name]
                ?: throw AppConfigException(
                    unknownPlace(),
                    "Attribute '%s' is not defined.".format(attribute.name)
                )
            addFiled(this, attr)
        }

        /**
         * Operation to map field to attribute
         *
         * @receiver Field
         * @param attribute The field related attribute
         */
        @ConfigDsl
        infix fun Field.from(attribute: DocumentMetadata.Attribute) {
            if (toAttribute) {
                throw AppConfigException(unknownPlace(), "Use 'to' mapping operator.")
            }
            addFiled(this, attribute)
        }

        /**
         * Operation to map field to attribute
         *
         * @receiver The field
         * @param attribute The attribute
         */
        infix fun Field.from(attribute: KProperty1<out Document, *>) {
            if (toAttribute) {
                throw AppConfigException(unknownPlace(), "Use 'to' mapping operator.")
            }
            val attr = entityType.documentMetadata.attributes[attribute.name]
                ?: throw AppConfigException(
                    unknownPlace(),
                    "Attribute '%s' is not defined.".format(attribute.name)
                )
            addFiled(this, attr)
        }

        /**
         * Operation to map field to attribute
         *
         * @receiver Field
         * @param attribute AttributeWithName
         */
        @ConfigDsl
        infix fun Field.from(attribute: AttributeWithName) {
            if (toAttribute) {
                throw AppConfigException(unknownPlace(), "Use 'to' mapping operator.")
            }
            val attr = entityType.documentMetadata.attributes[attribute.name]
                ?: throw AppConfigException(
                    unknownPlace(),
                    "Attribute '%s' is not defined.".format(attribute.name)
                )
            addFiled(this, attr)
        }

        /**
         * Build field set
         *
         * @return FieldSet
         */
        fun build(): FieldSet {
            if (lastField != null) addFiled(lastField!!, null)
            val name = this.name
            if (withSourceHeaders && mainSet) collectFieldsFromSources()
            validateAtOneLeastFieldDefined()
            validateFieldHasUniqueColumn()
            return FieldSet(name, mainSet, FieldAttributeMap(fields))
        }

        private fun collectFieldsFromSources() {
            checkSourcesNotNull(sources)
            checkHeaderRowDefined(headerRow)
            val collectedFields = try {
                readFieldNamesFromSource(sources, headerRow)
            } catch (e: AppConfigException) {
                throw AppConfigException(e.place, e.message, e)
            }
            collectedFields.mapNotNull { field ->
                val configuredField = fields.keys.find { it == field }
                if (configuredField != null) {
                    val attr = fields[configuredField]!!
                    fields.remove(configuredField)
                    Pair(
                        Field(configuredField.name, field.column, configuredField.regex),
                        attr
                    )
                } else {
                    val attr = entityType.documentMetadata.attributes.values.find { it.fieldName == field.name }
                    if (attr == null) {
                        helper.logger.warn(
                            "Entity type '{}' has no attribute '{}' and field '{}' will be skipped.",
                            entityType.qualifiedName,
                            field.name,
                            field.name
                        )
                        null
                    } else {
                        Pair(field, attr)
                    }
                }
            }.forEach {
                fields[it.first] = it.second
            }
        }

        private fun validateFieldHasUniqueColumn() {
            val dupColumns = fields.keys.filter { it.column != UNDEFINED_COLUMN }.groupBy { it.column }
            if (dupColumns.any { it.value.size > 1 }) {
                throw AppConfigException(
                    unknownPlace(),
                    "Field columns '${dupColumns.filter { it.value.size > 1 }.keys.joinToString(", ")}' are duplicated."
                )
            }
        }

        private fun validateAtOneLeastFieldDefined() {
            if (fields.isEmpty() && !withSourceHeaders) {
                throw AppConfigException(unknownPlace(), "At least one field must be defined for field set.")
            }
        }
    }

    /**
     * Helper class for the attribute with the name
     *
     * @property name String The attribute name
     * @constructor
     */
    data class AttributeWithName(val name: String)
}

private fun checkSourcesNotNull(sources: SourceDataCollection?) {
    contract {
        returns() implies (sources != null)
    }
    if (sources == null) {
        throw AppConfigException(unknownPlace(), "Sources must be defined before source field sets.")
    }
}

private fun checkHeaderRowDefined(headerRow: Int?) {
    contract {
        returns() implies (headerRow != null)
    }
    if (headerRow == null && headerRow != UNDEFINED_ROW) {
        throw AppConfigException(unknownPlace(), "Header row must be defined before source field sets.")
    }
}
