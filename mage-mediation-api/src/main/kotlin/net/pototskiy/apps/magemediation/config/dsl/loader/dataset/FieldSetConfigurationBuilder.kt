package net.pototskiy.apps.magemediation.config.dsl.loader.dataset

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dataset.Field.Companion.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.config.dataset.FieldSetType
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldSetConfiguration

@ConfigDsl
class FieldSetConfigurationBuilder(
    private var name: String? = null,
    private val type: FieldSetType
) {

    private var fields = mutableListOf<FieldConfiguration>()

    @Suppress("unused")
    fun FieldSetConfigurationBuilder.name(name: String) {
        this.name = name
    }

    @Suppress("unused")
    fun FieldSetConfigurationBuilder.field(name: String, block: FieldConfigurationBuilder.() -> Unit) {
        fields.add(FieldConfigurationBuilder(name).apply(block).build())
    }

    fun build(): FieldSetConfiguration {
        val name = this.name ?: throw ConfigException("Field set name should be configured")
        validateAtLeastFieldDefined()
        validateFiledHasUniqueName()
        validateFieldHasUniqueColumn()
        validateNestedParentPaired()
        validateNestedParentHasNoCycle()
        return FieldSetConfiguration(name, type, fields)
    }

    private fun validateNestedParentHasNoCycle() {
        for (f in fields.filter { it.nested }) {
            val visited = mutableListOf<FieldConfiguration>()
            visited.add(f)
            var v = f
            do {
                v = fields.find { it.name == v.parent }!!
                if (visited.any { it.name == v.name }) {
                    throw ConfigException("Filed<${f.name}> has cycle in nested chain")
                }
                visited.add(v)
            } while (v.nested)

        }
    }

    private fun validateNestedParentPaired() {
        val names = fields.map { it.name }
        val parentNames = fields.filter { it.nested }.map { it.parent }
        if (!names.containsAll(parentNames)) {
            val wrongParents = parentNames.minus(names)
            val wrongFields = fields.filter { it.nested && wrongParents.contains(it.parent) }
                .map { it.name }
                .joinToString(", ")
            throw ConfigException("Fields<$wrongFields> have wrong parents")
        }
    }

    private fun validateFieldHasUniqueColumn() {
        val dupColumns = fields.filter { it.column != UNDEFINED_COLUMN }.groupBy { it.column }
        if (dupColumns.any { it.value.size > 1 }) {
            throw ConfigException(
                "Field columns<${dupColumns.filter { it.value.size > 1 }.keys.joinToString(", ")}> are duplicated"
            )
        }
    }

    private fun validateFiledHasUniqueName() {
        val dupNames = fields.groupBy { it.name }
        if (dupNames.any { it.value.size > 1 }) {
            throw ConfigException(
                "Field names<${dupNames.filter { it.value.size > 0 }.keys.joinToString(", ")} are duplicated>"
            )
        }
    }

    private fun validateAtLeastFieldDefined() {
        if (fields.isEmpty()) {
            throw ConfigException("At least one field must be defined for field set")
        }
    }
}