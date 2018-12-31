package net.pototskiy.apps.magemediation.database.source

import org.jetbrains.exposed.dao.IntIdTable

abstract class SourceDataTable(table: String) : IntIdTable(table) {
    val touchedInLoading = bool("touched_in_loading").index()
    val previousStatus = varchar("previous_status", 50).index()
    val currentStatus = varchar("current_status", 50).index()
    val createdInMedium = datetime("created_in_medium").index()
    val updatedInMedium = datetime("updated_in_medium").index()
    val removedInMedium = datetime("removed_in_medium").nullable().index()
    val absentDays = integer("absent_days").index()
}
