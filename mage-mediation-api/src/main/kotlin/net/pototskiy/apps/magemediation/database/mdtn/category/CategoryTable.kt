package net.pototskiy.apps.magemediation.database.mdtn.category

import net.pototskiy.apps.magemediation.database.mdtn.MediumDataTable

abstract class CategoryTable(table: String): MediumDataTable(table) {
    val entityID = long("entity_id").uniqueIndex()
}