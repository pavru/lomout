package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.unknownPlace
import java.io.File
import java.util.*

/**
 * Source file collection
 *
 * @property files List<SourceFileDefinition>
 * @constructor
 */
data class SourceFileCollection(private val files: List<SourceFileDefinition>) : List<SourceFileDefinition> by files {
    /**
     * Source file collection builder
     *
     * @property helper ConfigBuildHelper
     * @property files MutableList<SourceFileDefinition>
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private val files = mutableListOf<SourceFileDefinition>()

        /**
         * File definition
         *
         * ```
         * ...
         *  file("file id") {
         *      path("file path")
         *      locale("ll_CC")
         *  }
         * ...
         * ```
         * * file — define the file with id
         * * [path][PathBuilder.path] — define file path, **mandatory**
         * * [locale][PathBuilder.locale] — define file locale, optional
         *
         * @see PathBuilder
         *
         * @param id String The file unique id
         * @param block The file definition
         */
        fun file(id: String, block: PathBuilder.() -> Unit) {
            val (file, locale) = PathBuilder().apply(block).build()
            val sourceFile = SourceFileDefinition(id, file, locale)
            files.add(sourceFile)
            helper.definedSourceFiles.register(sourceFile)
        }

        /**
         * Build source file collection
         *
         * @return SourceFileCollection
         */
        fun build() = SourceFileCollection(files)

        /**
         * File path builder
         *
         * @property path String?
         * @property locale String
         */
        class PathBuilder {
            private var path: String? = null
            private var locale: String = DEFAULT_LOCALE_STR

            /**
             * File path
             *
             * @param path String
             */
            fun path(path: String) {
                this.path = path
            }

            /**
             * File locale, default: *system locale*
             *
             * @param locale String
             */
            fun locale(locale: String) {
                this.locale = locale
            }

            /**
             * Path/locale build function
             *
             * @return Pair<File, Locale>
             */
            fun build(): Pair<File, Locale> {
                return Pair(
                    File(path ?: throw AppConfigException(unknownPlace(), "File path must be defined.")),
                    locale.createLocale()
                )
            }
        }
    }
}
