package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.UNDEFINED_ROW
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.source.Field
import net.pototskiy.apps.magemediation.api.source.FieldAttributeMap
import net.pototskiy.apps.magemediation.api.source.FieldCollection
import net.pototskiy.apps.magemediation.api.source.readFieldNamesFromSource
import kotlin.collections.set
import kotlin.contracts.contract

data class FieldSet(
    val name: String,
    val mainSet: Boolean,
    val fieldToAttr: FieldAttributeMap
) {
    val fields: FieldCollection = fieldToAttr.fields

    @Suppress("TooManyFunctions")
    @ConfigDsl
    class Builder(
        val typeManager: EntityTypeManager,
        private val entityType: EntityType,
        private val name: String,
        private val mainSet: Boolean = false,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?
    ) {
        private var fields = mutableMapOf<Field, Attribute<*>>()
        @ConfigDsl
        var lastFieldName: String? = null
        private var lastField: Field? = null

        fun field(name: String, block: Field.Builder.() -> Unit = {}): Field {
            if (lastField != null) {
                addFiled(lastField!!, null)
            }
            this.lastFieldName = name
            return Field.Builder(name, fields).apply(block).build().also {
                lastField = it
            }
        }

        private fun addFiled(lastField: Field, lastAttribute: Attribute<*>?) {
            fields[lastField] = lastAttribute
                ?: typeManager.getEntityAttribute(entityType, lastField.name)
                        ?: typeManager.createAttribute(
                    lastField.name,
                    StringType::class
                ) {
                    key(false)
                    nullable(true)
                    auto(true)
                }
            this.lastField = null
        }

        inline fun <reified T : Type> attribute(
            name: String? = lastFieldName,
            block: Attribute.Builder<T>.() -> Unit
        ): Attribute<*> =
            Attribute.Builder<T>(
                typeManager,
                name ?: throw ConfigException("Attribute name should be defined"),
                T::class
            ).apply(block).build()

        fun attribute(name: String) = AttributeWithName(name)

        infix fun Field.to(attribute: Attribute<*>) = addFiled(this, attribute)

        infix fun Field.to(attribute: AttributeWithName) {
            val attr = typeManager.getEntityAttribute(entityType, attribute.name)
                ?: throw ConfigException("Attribute<$attribute> is not defined")
            addFiled(this, attr)
        }

        fun build(): FieldSet {
            if (lastField != null) addFiled(lastField!!, null)
            val name = this.name
            if (withSourceHeaders && mainSet) collectFieldsFromSources()
            validateAtOneLeastFieldDefined()
            validateFiledHasUniqueName()
            validateFieldHasUniqueColumn()
            validateNestedParentPaired()
            validateNestedParentHasNoCycle()
            return FieldSet(name, mainSet, FieldAttributeMap(fields))
        }

        private fun collectFieldsFromSources() {
            checkSourcesNotNull(sources)
            checkHeaderRowDefined(headerRow)
            val collectedFields = try {
                readFieldNamesFromSource(sources, headerRow)
            } catch (e: ConfigException) {
                throw ConfigException("Can not collect headers (fields) from sources", e)
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
                    val attr = typeManager.getEntityAttribute(entityType, field.name)
                        ?: typeManager.createAttribute(
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

        private fun validateNestedParentHasNoCycle() {
            for (f in fields.keys.filter { it.isNested }) {
                val visited = mutableListOf<Field>()
                visited.add(f)
                var v = f
                do {
                    v = fields.keys.find { it.name == v.parent?.name }!!
                    if (visited.any { it.name == v.name }) {
                        throw ConfigException("Filed<${f.name}> has cycle in nested chain")
                    }
                    visited.add(v)
                } while (v.isNested)
            }
        }

        private fun validateNestedParentPaired() {
            val names = fields.keys.map { it.name }
            val parentNames = fields.keys.filter { it.isNested }.map { it.parent?.name }
            if (!names.containsAll(parentNames)) {
                val wrongParents = parentNames.minus(names)
                val wrongFields = fields.keys.filter { it.isNested && wrongParents.contains(it.parent?.name) }
                    .joinToString(", ") { it.name }
                throw ConfigException("Fields<$wrongFields> have wrong parents")
            }
        }

        private fun validateFieldHasUniqueColumn() {
            val dupColumns = fields.keys.filter { it.column != UNDEFINED_COLUMN }.groupBy { it.column }
            if (dupColumns.any { it.value.size > 1 }) {
                throw ConfigException(
                    "Field columns<${dupColumns.filter { it.value.size > 1 }.keys.joinToString(", ")}> are duplicated"
                )
            }
        }

        private fun validateFiledHasUniqueName() {
            val dupNames = fields.keys.groupBy { it.name }
            if (dupNames.any { it.value.size > 1 }) {
                throw ConfigException(
                    "Field names<${dupNames.filter { it.value.size > 1 }.keys.joinToString(", ")} are duplicated>"
                )
            }
        }

        private fun validateAtOneLeastFieldDefined() {
            if (fields.isEmpty() && !withSourceHeaders) {
                throw ConfigException("At least one field must be defined for field set")
            }
        }
    }

    data class AttributeWithName(val name: String)
}

private fun checkSourcesNotNull(sources: SourceDataCollection?) {
    contract {
        returns() implies (sources != null)
    }
    if (sources == null) {
        throw ConfigException("Sources must be defined before source field sets")
    }
}

private fun checkHeaderRowDefined(headerRow: Int?) {
    contract {
        returns() implies (headerRow != null)
    }
    if (headerRow == null && headerRow != UNDEFINED_ROW) {
        throw ConfigException("Header row must be defined before source field sets")
    }
}
