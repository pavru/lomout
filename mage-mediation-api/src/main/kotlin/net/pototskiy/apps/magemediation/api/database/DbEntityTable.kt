package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.entity.entityType
import org.jetbrains.exposed.dao.IntIdTable

object DbEntityTable : IntIdTable("entity") {
    val entityType = entityType("entity_type").index()
    val touchedInLoading = bool("touched_in_loading").index()
    val previousStatus = entityStatus("previous_status").nullable().index()
    val currentStatus = entityStatus("current_status").index()
    val created = datetime("created").index()
    val updated = datetime("updated").index()
    val removed = datetime("removed").nullable().index()
    val absentDays = integer("absent_days").index()
}
