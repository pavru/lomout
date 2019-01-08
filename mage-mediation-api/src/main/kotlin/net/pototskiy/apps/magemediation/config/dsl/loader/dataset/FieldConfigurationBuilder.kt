package net.pototskiy.apps.magemediation.config.dsl.loader.dataset

import net.pototskiy.apps.magemediation.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.dsl.type.AttributeTypeBuilder
import net.pototskiy.apps.magemediation.config.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.config.type.AttributeType

@ConfigDsl
class FieldConfigurationBuilder(private val name: String) {
    private var column: Int? = null
    private var regex: String? = null
    private var type: AttributeType? = null
    private var keyField: Boolean = false
    private var nested: Boolean = false
    private var parent: String? = null
    private var optional: Boolean = false

    @Suppress("unused")
    fun FieldConfigurationBuilder.column(column: Int): FieldConfigurationBuilder = this.apply { this.column = column }

    @Suppress("unused")
    fun FieldConfigurationBuilder.regex(regex: String): FieldConfigurationBuilder = this.apply { this.regex = regex }

    @Suppress("unused")
    fun FieldConfigurationBuilder.type(block: AttributeTypeBuilder.() -> Unit): FieldConfigurationBuilder =
        this.apply { type = AttributeTypeBuilder().apply(block).build() }

    @Suppress("unused")
    fun FieldConfigurationBuilder.key(): FieldConfigurationBuilder = this.apply { keyField = true }

    @Suppress("unused")
    fun FieldConfigurationBuilder.nested(): FieldConfigurationBuilder = this.apply { nested = true }

    @Suppress("unused")
    fun FieldConfigurationBuilder.parent(parent: String): FieldConfigurationBuilder =
        this.apply { this.parent = parent }

    @Suppress("unused")
    fun FieldConfigurationBuilder.optional(): FieldConfigurationBuilder = this.apply { optional = true }

    fun build(): FieldConfiguration {
        val column = this.column ?: UNDEFINED_COLUMN
        val type = this.type ?: AttributeTypeBuilder().build()
        val regex = regex?.let { Regex(it) }
        validateNestedIsNotKey()
        return FieldConfiguration(
            name,
            column,
            regex,
            type,
            keyField,
            nested,
            parent,
            optional
        )
    }

    private fun validateNestedIsNotKey() {
        if (nested && keyField) {
            throw ConfigException("Nested field can not be key field")
        }
    }
}