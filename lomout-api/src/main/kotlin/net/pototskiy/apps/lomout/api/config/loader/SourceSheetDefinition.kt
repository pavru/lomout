package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppSheetException

/**
 * Source data sheet
 *
 * @property name The sheet name
 * @property pattern The sheet regex pattern
 * @property definition The sheet definition string presentation
 * @constructor
 */
data class SourceSheetDefinition(
    val name: String? = null,
    val pattern: Regex? = null
) {
    /**
     * Test if sheet name match with definition
     *
     * @param sheet The sheet name
     * @return Boolean
     */
    fun isMatch(sheet: String): Boolean {
        return when {
            this.name != null -> sheet == this.name
            this.pattern != null -> this.pattern.matches(sheet)
            else -> throw AppSheetException("Source sheet is not defined")
        }
    }

    val definition: String
        get() = when {
            this.name != null -> "name:$name"
            this.pattern != null -> "regex:Regex($pattern)"
            else -> "name:???"
        }
}
