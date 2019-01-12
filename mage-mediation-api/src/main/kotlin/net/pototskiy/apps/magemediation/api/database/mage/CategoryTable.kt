package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.source.SourceDataTable

abstract class CategoryTable(name:String): SourceDataTable(name) {
    val entityID = long("entity_id").uniqueIndex()
}