package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.UNDEFINED_COLUMN
import net.pototskiy.apps.lomout.api.config.ConfigDsl

/**
 * Source data field
 *
 * @property name String
 * @property column Int
 * @property regex Regex?
 * @constructor
 * @param name String The field name
 * @param column Int The field column, zero based
 * @param regex Regex? The field pattern
 */
data class Field(
    val name: String,
    val column: Int,
    val regex: Regex?
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
     * Field builder class
     *
     * @property name String
     * @property column Int?
     * @property regex Regex?
     * @property parent Field?
     * @constructor
     * @param name String The field name to build
     */
    @ConfigDsl
    class Builder(private var name: String) {
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
         * * number — field column number, zero based
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
         * * regex — regular expression string
         *
         * @param regex String
         */
        fun pattern(regex: String) {
            this.regex = Regex(regex)
        }

        /**
         * Build field
         *
         * @return Field
         */
        fun build(): Field {
            return Field(name, column ?: UNDEFINED_COLUMN, regex)
        }
    }
}
