package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.source.SourceDataTable

abstract class ProductTable(name: String): SourceDataTable(name) {
    val sku = varchar("sku", 100).uniqueIndex()
}