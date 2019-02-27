package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.ENTITY_TYPE_NAME_LENGTH
import org.jetbrains.exposed.dao.IntIdTable

object DbEntityTable : IntIdTable("entity") {
    val entityType = varchar("entity_type", ENTITY_TYPE_NAME_LENGTH).index()
    val touchedInLoading = bool("touched_in_loading").index()
    val previousStatus = entityStatus("previous_status").nullable().index()
    val currentStatus = entityStatus("current_status").index()
    val created = datetime("created").index()
    val updated = datetime("updated").index()
    val removed = datetime("removed").nullable().index()
    val absentDays = integer("absent_days").index()
}
