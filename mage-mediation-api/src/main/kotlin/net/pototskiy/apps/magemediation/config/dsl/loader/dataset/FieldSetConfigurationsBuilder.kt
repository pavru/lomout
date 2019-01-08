package net.pototskiy.apps.magemediation.config.dsl.loader.dataset

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.FieldSetType
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.loader.dataset.FieldSetConfiguration

@ConfigDsl
class FieldSetConfigurationsBuilder {
    private val sets = mutableListOf<FieldSetConfiguration>()

    @Suppress("unused")
    fun FieldSetConfigurationsBuilder.main(
        name: String? = null,
        block: FieldSetConfigurationBuilder.() -> Unit
    ) {
        sets.findLast { it.type == FieldSetType.MAIN }?.let {
            throw ConfigException("Main field set<${it.name}> has already configured")
        }
        sets.add(FieldSetConfigurationBuilder(name, FieldSetType.MAIN).apply(block).build())
    }

    @Suppress("unused")
    fun FieldSetConfigurationsBuilder.additional(
        name: String? = null,
        block: FieldSetConfigurationBuilder.() -> Unit
    ) {
        sets.add(FieldSetConfigurationBuilder(name, FieldSetType.ADDITIONAL).apply(block).build())
    }

    fun build(): List<FieldSetConfiguration> {
        validateMainSetExists()
        validateFieldSetHasUniqueName()
        validateMainSetHasKeyField()
        validateDiscriminationPossible()
        validateAllFieldNameUnique()
        return sets.toList()
    }

    private fun validateAllFieldNameUnique() {
        val dupNames = sets.flatMap { it.fields }.groupBy { it.name }
        if (dupNames.any { it.value.size > 1 }) {
            throw ConfigException("Field names<${dupNames.filter { it.value.size > 1 }.keys.joinToString(", ")}> are duplicated in dataset")
        }
    }

    private fun validateDiscriminationPossible() {
        if (sets.size > 2 && !sets.all { set -> set.fields.any { it.regex != null } }) {
            throw ConfigException("Dataset has more then one fieldset, but not all fieldset has field with regex")
        }
    }

    private fun validateMainSetHasKeyField() {
        if (sets.find { it.type == FieldSetType.MAIN }?.fields?.any { it.keyField } != true) {
            throw ConfigException("Main field set has no key field")
        }
    }

    private fun validateFieldSetHasUniqueName() {
        val dupNames = sets.groupBy { it.name }
        if (dupNames.any { it.value.size > 1 }) {
            throw ConfigException("Field set names<${dupNames.filter { it.value.size > 1 }.keys.joinToString(", ")}> are duplicated")
        }
    }

    private fun validateMainSetExists() {
        if (!sets.any { it.type == FieldSetType.MAIN }) {
            throw ConfigException("Field sets should contain at least one main field set")
        }
    }
}