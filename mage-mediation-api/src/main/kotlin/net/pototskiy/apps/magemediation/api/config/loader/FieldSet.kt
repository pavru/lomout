package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.AttributeCollection
import net.pototskiy.apps.magemediation.api.config.data.Field
import net.pototskiy.apps.magemediation.api.config.data.FieldCollection
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType

data class FieldSet(
    val name: String,
    val mainSet: Boolean,
    val fields: FieldCollection
) {
    @ConfigDsl
    class Builder(
        private var name: String,
        private val mainSet: Boolean = false,
        private val attributes: AttributeCollection,
        private val withSourceHeaders: Boolean
    ) {
        private var fields = mutableMapOf<Field, Attribute>()
        private var lastFieldName: String? = null

        @Suppress("unused")
        fun Builder.name(name: String): Builder = this.apply { this.name = name }

        @Suppress("unused")
        fun Builder.field(name: String, block: Field.Builder.() -> Unit = {}): Field {
            this.lastFieldName = name
            val attr = attributes.find { it.name == name }
                ?:Attribute(name, AttributeStringType(false), false, false, true, null)
            return Field.Builder(name, fields).apply(block).build().also {
                fields[it] = attr
            }
        }

        @Deprecated(
            "Remove after migration to new schema",
            ReplaceWith("attribute")
        )
        @Suppress("unused")
        fun Builder.attribute(
            name: String? = lastFieldName,
            block: Attribute.Builder.() -> Unit
        ): Attribute =
            Attribute.Builder(name ?: throw ConfigException("Attribute name should be defined"))
                .apply(block).build()

        @Suppress("unused")
        fun Builder.attribute(name: String) = AttributeName(name)

        @Deprecated(
            "Will be removed after migration to new schema",
            ReplaceWith("to")
        )
        infix fun Field.to(attribute: Attribute) = fields.put(this, attribute)

        infix fun Field.to(attribute: AttributeName) {
            val attr = attributes.find { it.name == attribute.name }
                ?: throw ConfigException("Attribute<${attribute.name}> is not defined")
            fields[this] = attr
        }

        fun build(): FieldSet {
            val name = this.name
            if (withSourceHeaders && fields.isEmpty()) {
                attributes.forEach {
                    fields[Field(it.name, UNDEFINED_COLUMN,null,null,null)] = it
                }
            }
            validateAtOneLeastFieldDefined()
            validateFiledHasUniqueName()
            validateFieldHasUniqueColumn()
            validateNestedParentPaired()
            validateNestedParentHasNoCycle()
            return FieldSet(name, mainSet, FieldCollection(fields))
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

        data class AttributeName(val name: String)
    }

}
