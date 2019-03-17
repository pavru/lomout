package net.pototskiy.apps.magemediation.api.config.printer

data class PrinterLineCollection(private val lines: List<PrinterLine>) : List<PrinterLine> by lines
