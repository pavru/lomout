package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin
import kotlin.reflect.full.createInstance


data class Field(
    val name: String,
    val column: Int,
    val regex: Regex?,
    val parent: Field?,
    val transformer: NewTransformer<Any?, Any?>?
) {
    fun isMatchToPattern(value: Any): Boolean = regex?.matches(value.toString()) ?: true
    fun transform(value: Any?): Any? = transformer?.transform(value) ?: value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Field) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    val isNested get() = parent != null

    @ConfigDsl
    class Builder(
        private var name: String,
        private val definedFields: Map<Field, Attribute>
    ) {
        private var column: Int? = null
        private var regex: Regex? = null
        private var parent: Field? = null
        @Suppress("PropertyName")
        var __transformer: NewTransformer<Any?, Any?>? = null

        @Suppress("unused")
        fun Builder.column(column: Int): Builder = this.apply { this.column = column }

        @Suppress("unused")
        fun Builder.pattern(regex: String): Builder = this.apply { this.regex = Regex(regex) }

        @Suppress("unused")
        fun Builder.parent(parent: String): Builder = this.apply {
            val field = definedFields.keys.find { it.name == parent }?.takeIf {
                definedFields[it]?.type is AttributeAttributeListType
            } ?: throw ConfigException("Parent field<$parent> must be defined and has type attributeList")
            this.parent = field
        }

        @Suppress("unused")
        @JvmName("with_transform__plugin")
        inline fun <reified P : NewValueTransformPlugin<T, R>, T : Any?, R : Any?> Builder.withTransform(): Builder =
            this.apply {
                @Suppress("UNCHECKED_CAST")
                __transformer = NewTransformerWithPlugin(P::class) as NewTransformer<Any?, Any?>
            }

        @Suppress("unused")
        @JvmName("with_transform__plugin__options")
        inline fun <reified P : NewValueTransformPlugin<T, R>,
                O : NewPlugin.Options,
                T : Any?,
                R : Any?> Builder.withTransform(
            block: O.() -> Unit
        ): Builder = this.apply {
            val plugin = P::class.createInstance()
            @Suppress("UNCHECKED_CAST") val options = (plugin.optionSetter() as O).apply(block)
            @Suppress("UNCHECKED_CAST")
            __transformer = NewTransformerWithPlugin(P::class, options) as NewTransformer<Any?, Any?>
        }

        @Suppress("unused")
        @JvmName("with_transform__function")
        fun <T : Any?, R : Any?> Builder.withTransform(block: NewValueTransformFunction<T, R>): Builder =
            this.apply {
                @Suppress("UNCHECKED_CAST")
                __transformer = NewTransformerWithFunction(block) as NewTransformer<Any?, Any?>
            }

        fun build(): Field {
            return Field(
                name,
                column ?: UNDEFINED_COLUMN,
                regex,
                parent,
                __transformer
            )
        }
    }
}
