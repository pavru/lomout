package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.loader.SourceFileCollection

/**
 * Printer part configuration
 *
 * @property files SourceFileCollection Files to print
 * @property lines PrinterLineCollection Printer lines
 * @constructor
 */
data class PrinterConfiguration(
    val files: SourceFileCollection,
    val lines: PrinterLineCollection
) {
    /**
     * Printer configuration builder class
     *
     * @property helper ConfigBuildHelper
     * @property files SourceFileCollection?
     * @property lines MutableList<PrinterLine>
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var files: SourceFileCollection? = null
        private var lines = mutableListOf<PrinterLine>()

        /**
         * Define printer files like in the loader
         *
         * ```
         * ...
         *  files {
         *      file("file id") { path("file path"); locale("cc_LL") }
         *      file("file id") {
         *          path("file path")
         *          locale("cc_LL")
         *      }
         *      ...
         *  }
         * ...
         * ```
         * * [file][SourceFileCollection.Builder.file] - define file id, **mandatory**
         * * [path][SourceFileCollection.Builder.PathBuilder.path] - define file path, **mandatory**
         * * [locale][SourceFileCollection.Builder.PathBuilder.locale] - define file locale, optional
         *
         * @see SourceFileCollection
         *
         * @param block Files definition
         */
        @ConfigDsl
        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            this.files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        /**
         * Define printer line, **mandatory**
         *
         * ```
         * ...
         *  printerLine {
         *      input {...}
         *      output {...}
         *      pipeline {...}
         *  }
         * ...
         * ```
         * * [input][PrinterLine.Builder.input] - printer line input entities, **mandatory**
         * * [output][PrinterLine.Builder.output] - printer line output, **mandatory**
         * * [pipeline][PrinterLine.Builder.pipeline] - printer line processing pipeline, **mandatory**
         *
         * @param block PrinterLine.Builder.() -> Unit
         */
        @ConfigDsl
        fun printerLine(block: PrinterLine.Builder.() -> Unit) {
            lines.add(PrinterLine.Builder(helper).apply(block).build())
        }

        /**
         * Build printer configuration
         *
         * @return PrinterConfiguration
         */
        fun build(): PrinterConfiguration {
            return PrinterConfiguration(
                files ?: throw AppConfigException("Printer files must be defined"),
                PrinterLineCollection(lines)
            )
        }
    }
}
