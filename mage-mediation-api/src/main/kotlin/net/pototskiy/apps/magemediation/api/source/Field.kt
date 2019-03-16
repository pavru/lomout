package net.pototskiy.apps.magemediation.api.source

import net.pototskiy.apps.magemediation.api.AppConfigException
import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType

data class Field(
    val name: String,
    val column: Int,
    val regex: Regex?,
    val parent: Field?
) {
    fun isMatchToPattern(value: Any): Boolean = regex?.matches(value.toString()) ?: true

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
        private val definedFields: Map<Field, Attribute<*>>
    ) {
        private var column: Int? = null
        private var regex: Regex? = null
        private var parent: Field? = null

        fun column(column: Int) {
            this.column = column
        }

        fun pattern(regex: String) {
            this.regex = Regex(regex)
        }

        fun parent(parent: String) {
            val field = definedFields.keys.find { it.name == parent }?.takeIf {
                definedFields[it]?.valueType == AttributeListType::class
            } ?: throw AppConfigException("Parent field<$parent> must be defined and has type attribute list")
            this.parent = field
        }

        fun build(): Field {
            return Field(
                name,
                column ?: UNDEFINED_COLUMN,
                regex,
                parent
            )
        }
    }
}
