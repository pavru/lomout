package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.EmptyRowStrategy

data class SourceData(
    val file: SourceFileDefinition,
    val sheet: SourceSheetDefinition,
    val emptyRowStrategy: EmptyRowStrategy
) {
    @ConfigDsl
    class Builder() {
        private var file: SourceFileDefinition? = null
        private var sheet: SourceSheetDefinition = SourceSheetDefinition(null, Regex(".*"))
        private var emptyRowStrategy: EmptyRowStrategy = EmptyRowStrategy.IGNORE
        @Suppress("unused")
        fun Builder.file(id: String): Builder =
            this.apply {
                file = Config.Builder.definedSourceFiles.findRegistered(id)
                    ?: throw ConfigException("Source file<id:$id> is not defined")
            }

        @Suppress("unused")
        fun Builder.sheet(sheet: String): Builder =
            this.apply { this.sheet = SourceSheetDefinition(name = sheet) }

        @Suppress("unused")
        fun Builder.sheet(sheet: Regex): Builder =
            this.apply { this.sheet = SourceSheetDefinition(pattern = sheet) }

        @Suppress("unused")
        fun Builder.stopOnEmptyRow(): Builder =
            this.apply { emptyRowStrategy = EmptyRowStrategy.STOP }

        @Suppress("unused")
        fun Builder.ignoreEmptyRows(): Builder =
            this.apply { emptyRowStrategy = EmptyRowStrategy.IGNORE }

        fun build(): SourceData = SourceData(
            this.file ?: throw ConfigException("File id is not defined"),
            this.sheet,
            this.emptyRowStrategy
        )
    }
}
