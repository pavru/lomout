package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.createLocale
import java.io.File
import java.util.*

data class SourceFileCollection(private val files: List<SourceFileDefinition>) : List<SourceFileDefinition> by files {
    @ConfigDsl
    class Builder {
        private val files = mutableListOf<SourceFileDefinition>()

        fun file(id: String, block: PathBuilder.() -> Unit) {
            val (file, locale) = PathBuilder().apply(block).build()
            val sourceFile = SourceFileDefinition(id, file, locale)
            files.add(sourceFile)
            Config.Builder.definedSourceFiles.register(sourceFile)
        }

        fun build() = SourceFileCollection(files)

        class PathBuilder {
            private var path: String? = null
            private var locale: String = DEFAULT_LOCALE_STR

            fun path(path: String) {
                this.path = path
            }

            fun locale(locale: String) {
                this.locale = locale
            }

            fun build(): Pair<File, Locale> {
                return Pair(
                    File(path ?: throw ConfigException("File path must be defined")),
                    locale.createLocale()
                )
            }
        }
    }
}
