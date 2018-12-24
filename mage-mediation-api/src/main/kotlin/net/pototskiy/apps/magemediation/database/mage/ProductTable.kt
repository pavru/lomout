package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class ProductTable(name: String): SourceDataTable(name) {
    val sku = varchar("sku", 100).uniqueIndex()
}