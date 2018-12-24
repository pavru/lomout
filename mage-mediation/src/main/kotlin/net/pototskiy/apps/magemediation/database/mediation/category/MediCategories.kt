package net.pototskiy.apps.magemediation.database.mediation.category

import net.pototskiy.apps.magemediation.database.mediation.MediTable

object MediCategories: MediTable("medium") {
    val entityID = long("entity_id").uniqueIndex()
}