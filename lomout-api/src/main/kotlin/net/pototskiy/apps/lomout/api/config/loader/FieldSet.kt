package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppAttributeException
import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.UNDEFINED_COLUMN
import net.pototskiy.apps.lomout.api.UNDEFINED_ROW
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.FieldAttributeMap
import net.pototskiy.apps.lomout.api.source.FieldCollection
import net.pototskiy.apps.lomout.api.source.readFieldNamesFromSource
import kotlin.collections.set
import kotlin.contracts.contract

/**
 * Field set
 *
 * @property name String The set name
 * @property mainSet Boolean The flag of main set
 * @property fieldToAttr FieldAttributeMap The field to attribute map
 * @property fields FieldCollection The fields collection
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
        private val entityType: EntityType,
        private val name: String,
        private val mainSet: Boolean = false,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?
    ) {
        private var fields = mutableMapOf<Field, Attribute<*>>()
        var lastFieldName: String? = null
        private var lastField: Field? = null

        /**
         * Define field.
         *
         * Fields of source without headers must have column definition.
         * Pattern is used to validate field value and determinate which field set must be used for row.
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
         * @param block Field.Builder.() -> Unit
         * @return Field
         */
        @ConfigDsl
        fun field(name: String, block: Field.Builder.() -> Unit = {}): Field {
            if (lastField != null) {
                addFiled(lastField!!, null)
            }
            this.lastFieldName = name
            return Field.Builder(name, fields).apply(block).build().also {
                lastField = it
            }
        }

        private fun addFiled(field: Field, lastAttribute: Attribute<*>?) {
            if (fields.containsKey(field)) {
                throw AppConfigException("Field<${field.name}> is already defined")
            }
            fields[field] = lastAttribute
                ?: helper.typeManager.getEntityAttribute(entityType, field.name)
                        ?: helper.typeManager.createAttribute(
                    field.name,
                    StringType::class
                ) {
                    key(false)
                    nullable(true)
                    auto(true)
                }
            this.lastField = null
        }

        /**
         * Define attribute which is used for field
         *
         * If name is omitted field name is used for attribute
         *
         * ```
         * ...
         *  field("name") to attribute {
         *      nullable()
         *      reader {...}
         *  }
         * ...
         * ```
         *
         * @param name The attribute name
         * @param block Attribute.Builder<T>.() -> Unit
         * @return Attribute<*>
         */
        @Generated
        @ConfigDsl
        inline fun <reified T : Type> attribute(
            name: String? = lastFieldName,
            block: Attribute.Builder<T>.() -> Unit
        ): Attribute<*> =
            Attribute.Builder(
                helper,
                name ?: throw AppAttributeException("Attribute name should be defined"),
                T::class
            ).apply(block).build()

        /**
         * Reference to attribute that is used for field
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
         * @param attribute The The field related attribute
         */
        @ConfigDsl
        infix fun Field.to(attribute: Attribute<*>) = addFiled(this, attribute)

        /**
         * Operation to map field to attribute
         *
         * @receiver Field
         * @param attribute AttributeWithName
         */
        @ConfigDsl
        infix fun Field.to(attribute: AttributeWithName) {
            val attr = helper.typeManager.getEntityAttribute(entityType, attribute.name)
                ?: throw AppAttributeException("Attribute<$attribute> is not defined")
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
                throw AppConfigException(e.message, e)
            }
            collectedFields.map { field ->
                val configuredField = fields.keys.find { it == field }
                if (configuredField != null) {
                    val attr = fields[configuredField]!!
                    fields.remove(configuredField)
                    Pair(
                        Field(configuredField.name, field.column, configuredField.regex, configuredField.parent),
                        attr
                    )
                } else {
                    val attr = helper.typeManager.getEntityAttribute(entityType, field.name)
                        ?: helper.typeManager.createAttribute(
                            field.name, StringType::class
                        ) {
                            key(false)
                            nullable(true)
                            auto(true)
                        }
//                            .also { typeManager.addEntityAttribute(entityType, it) }
                    Pair(field, attr)
                }
            }.forEach {
                fields[it.first] = it.second
            }
        }

        private fun validateFieldHasUniqueColumn() {
            val dupColumns = fields.keys.filter { it.column != UNDEFINED_COLUMN }.groupBy { it.column }
            if (dupColumns.any { it.value.size > 1 }) {
                throw AppConfigException(
                    "Field columns<${dupColumns.filter { it.value.size > 1 }.keys.joinToString(", ")}> are duplicated"
                )
            }
        }

        private fun validateAtOneLeastFieldDefined() {
            if (fields.isEmpty() && !withSourceHeaders) {
                throw AppConfigException("At least one field must be defined for field set")
            }
        }
    }

    /**
     * Helper class for attribute with name
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
        throw AppConfigException("Sources must be defined before source field sets")
    }
}

private fun checkHeaderRowDefined(headerRow: Int?) {
    contract {
        returns() implies (headerRow != null)
    }
    if (headerRow == null && headerRow != UNDEFINED_ROW) {
        throw AppConfigException("Header row must be defined before source field sets")
    }
}
