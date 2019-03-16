package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.AppSheetException

data class SourceSheetDefinition(
    private val name: String? = null,
    private val pattern: Regex? = null
) {
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
            this.pattern != null -> "name:Regex($pattern)"
            else -> "name:???"
        }
}
