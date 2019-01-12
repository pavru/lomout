package net.pototskiy.apps.magemediation.api.database.mdtn.category

import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataTable

abstract class CategoryTable(table: String): MediumDataTable(table) {
    val entityID = long("entity_id").uniqueIndex()
}