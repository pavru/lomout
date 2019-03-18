package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.entityType
import org.jetbrains.exposed.dao.IntIdTable

object DbEntityTable : IntIdTable("entity") {
    lateinit var entityTypeManager: EntityTypeManager
    val entityType = entityType("entity_type", this).index()
    val touchedInLoading = bool("touched_in_loading").index()
    val previousStatus = entityStatus("previous_status").nullable().index()
    val currentStatus = entityStatus("current_status").index()
    val created = datetime("created").index()
    val updated = datetime("updated").index()
    val removed = datetime("removed").nullable().index()
    val absentDays = integer("absent_days").index()
}
