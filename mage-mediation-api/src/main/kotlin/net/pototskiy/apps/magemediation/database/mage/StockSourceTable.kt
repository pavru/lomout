package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class StockSourceTable(name: String) : SourceDataTable(name) {
    val sourceCode = varchar("source_code", 100).index()
    val sku = varchar("sku", 300).index()
    val status = bool("status")
    val quantity = double("quantity")

    init {
        uniqueIndex(sourceCode, sku)
    }
}