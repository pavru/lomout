package net.pototskiy.apps.lomout.api.config.printer

/**
 * Printer line collection
 *
 * @property lines List<PrinterLine>
 * @constructor
 */
data class PrinterLineCollection(private val lines: List<PrinterLine>) : List<PrinterLine> by lines
