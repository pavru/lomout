package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.loader.SourceFileCollection

data class PrinterConfiguration(
    val files: SourceFileCollection,
    val lines: PrinterLineCollection
) {
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var files: SourceFileCollection? = null
        private var lines = mutableListOf<PrinterLine>()

        @ConfigDsl
        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            this.files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        @ConfigDsl
        fun printerLine(block: PrinterLine.Builder.() -> Unit) {
            lines.add(PrinterLine.Builder(helper).apply(block).build())
        }

        fun build(): PrinterConfiguration {
            return PrinterConfiguration(
                files ?: throw AppConfigException("Printer files must be defined"),
                PrinterLineCollection(lines)
            )
        }
    }
}
