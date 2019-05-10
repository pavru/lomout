package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.UNDEFINED_COLUMN
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeListType

/**
 * Source data field
 *
 * @property name String
 * @property column Int
 * @property regex Regex?
 * @property parent Field?
 * @property isNested Boolean
 * @constructor
 * @param name String The field name
 * @param column Int The field column, zero based
 * @param regex Regex? The field pattern
 * @param parent Field? The parent field for nested one
 */
data class Field(
    val name: String,
    val column: Int,
    val regex: Regex?,
    val parent: Field?
) {
    /**
     * Test if field value matches to field pattern
     *
     * @param value Any
     * @return Boolean
     */
    fun isMatchToPattern(value: Any): Boolean = regex?.matches(value.toString()) ?: true

    /**
     * Is fields are equal
     *
     * @param other Any?
     * @return Boolean
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Field) return false
        if (name != other.name) return false
        return true
    }

    /**
     * Field hash code
     *
     * @return Int
     */
    override fun hashCode(): Int {
        return name.hashCode()
    }

    /**
     * Is field nested one
     */
    val isNested get() = parent != null

    /**
     * Field builder class
     *
     * @property name String
     * @property definedFields Map<Field, Attribute<*>>
     * @property column Int?
     * @property regex Regex?
     * @property parent Field?
     * @constructor
     * @param name String The field name to build
     * @param definedFields Map<Field, Attribute<*>> The map of already defined fields
     */
    @ConfigDsl
    class Builder(
        private var name: String,
        private val definedFields: Map<Field, Attribute<*>>
    ) {
        private var column: Int? = null
        private var regex: Regex? = null
        private var parent: Field? = null

        /**
         * Field column, zero based
         *
         * ```
         * ...
         *  column(number)
         * ...
         * ```
         * * number - field column number, zero based
         *
         * @param column Int
         */
        fun column(column: Int) {
            this.column = column
        }

        /**
         * Field value pattern
         *
         * ```
         * ...
         *  pattern(regex)
         * ...
         * ```
         * * regex - regular expression string
         *
         * @param regex String
         */
        fun pattern(regex: String) {
            this.regex = Regex(regex)
        }

        /**
         * Parent field
         *
         * ```
         * ...
         *  parent(name)
         * ...
         * ```
         * * name - name of parent field, must be defined before
         *
         * @param parent String
         */
        fun parent(parent: String) {
            val field = definedFields.keys.find { it.name == parent }?.takeIf {
                definedFields[it]?.valueType == AttributeListType::class
            } ?: throw AppConfigException("Parent field<$parent> must be defined and has type attribute list")
            this.parent = field
        }

        /**
         * Build field
         *
         * @return Field
         */
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
