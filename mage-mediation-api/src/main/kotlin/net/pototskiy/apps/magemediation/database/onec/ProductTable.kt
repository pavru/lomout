package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class ProductTable(name:String): SourceDataTable(name) {
    val sku = varchar("sku", 100).uniqueIndex()
}