package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class CategoryTable(name:String): SourceDataTable(name) {
    val entityID = long("entity_id").uniqueIndex()
}