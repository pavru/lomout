package net.pototskiy.apps.magemediation.api.database.onec

import net.pototskiy.apps.magemediation.api.database.source.SourceDataTable

abstract class ProductTable(name:String): SourceDataTable(name) {
    val sku = varchar("sku", 100).uniqueIndex()
}