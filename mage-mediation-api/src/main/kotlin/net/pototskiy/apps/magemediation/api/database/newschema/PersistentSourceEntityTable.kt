package net.pototskiy.apps.magemediation.api.database.newschema

import net.pototskiy.apps.magemediation.api.database.source.sourceDataStatus

open class PersistentSourceEntityTable(table: String) : PersistentEntityTable(table) {
    val touchedInLoading = bool("touched_in_loading").index()
    val previousStatus = sourceDataStatus("previous_status").nullable().index()
    val currentStatus = sourceDataStatus("current_status").index()
    val createdInMedium = datetime("created_in_medium").index()
    val updatedInMedium = datetime("updated_in_medium").index()
    val removedInMedium = datetime("removed_in_medium").nullable().index()
    val absentDays = integer("absent_days").index()
}
