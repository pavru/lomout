package net.pototskiy.apps.magemediation.dsl.config.loader.dataset

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.dsl.config.type.AttributeTypeBuilder
import net.pototskiy.apps.magemediation.api.config.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.api.config.type.AttributeType
import net.pototskiy.apps.magemediation.api.plugable.loader.FieldTransformer
import kotlin.reflect.KClass

@ConfigDsl
class FieldConfigurationBuilder(private val name: String) {
    private var column: Int? = null
    private var regex: String? = null
    private var type: AttributeType? = null
    private var keyField: Boolean = false
    private var nested: Boolean = false
    private var parent: String? = null
    private var optional: Boolean = false
    private var transfomer: KClass<out FieldTransformer<out Any>>? = null

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

    @Suppress("unused")
    fun FieldConfigurationBuilder.transformer(klass: KClass<out FieldTransformer<out Any>>) = this.apply { transfomer = klass }

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
            optional,
            transfomer
        )
    }

    private fun validateNestedIsNotKey() {
        if (nested && keyField) {
            throw ConfigException("Nested field can not be key field")
        }
    }
}