package net.pototskiy.apps.lomout.api.config.loader

/**
 * Source data sheet
 *
 * @property definition The sheet definition string presentation
 * @constructor
 */
sealed class SourceSheetDefinition {
    /**
     * Test if sheet name match with definition
     *
     * @param sheet The sheet name
     * @return Boolean
     */
    fun isMatch(sheet: String): Boolean {
        return when (this) {
            is SourceSheetDefinitionWithName -> sheet == this.name
            is SourceSheetDefinitionWithPattern -> this.pattern.matches(sheet)
        }
    }

    val definition: String
        get() = when (this) {
            is SourceSheetDefinitionWithName -> "name:$name"
            is SourceSheetDefinitionWithPattern -> "regex:$pattern"
        }

    /**
     * Source sheet definition with sheet name
     *
     * @property name The sheet name
     * @constructor
     */
    data class SourceSheetDefinitionWithName(val name: String) : SourceSheetDefinition()

    /**
     * Source sheet definition with regular expression pattern
     *
     * @property pattern The sheet name pattern
     * @constructor
     */
    data class SourceSheetDefinitionWithPattern(val pattern: Regex) : SourceSheetDefinition()
}
