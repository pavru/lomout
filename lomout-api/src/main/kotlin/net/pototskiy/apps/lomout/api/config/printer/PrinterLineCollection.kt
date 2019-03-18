package net.pototskiy.apps.lomout.api.config.printer

data class PrinterLineCollection(private val lines: List<PrinterLine>) : List<PrinterLine> by lines
