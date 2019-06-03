@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.entityType
import org.jetbrains.exposed.dao.IntIdTable

/**
 * Exposed entity DB table
 */
object DbEntityTable : IntIdTable("entity") {
    /**
     * Entity type manager
     */
    lateinit var entityTypeManager: EntityTypeManager
    /**
     * Entity type
     */
    val entityType = entityType("entity_type", this).index()
    /**
     * Change flag
     */
    val touchedInLoading = bool("touched_in_loading").index()
    /**
     * Previous entity status
     */
    val previousStatus = entityStatus("previous_status").nullable().index()
    /**
     * Current entity status
     */
    val currentStatus = entityStatus("current_status").index()
    /**
     * Timestamp of creating
     */
    val created = datetime("created").index()
    /**
     * Timestamp of updating
     */
    val updated = datetime("updated").index()
    /**
     * Timestamp of removing
     */
    val removed = datetime("removed").nullable().index()
    /**
     * Absent (in a source) days
     */
    val absentDays = integer("absent_days").index()
}

/**
 * DbEntityTable synonym
 */
val EntityTab = DbEntityTable
/**
 * DbEntityTable.id synonym
 */
val EntityIdCol = DbEntityTable.id
/**
 * DbEntityTable.entityType synonym
 */
val EntityTypeCol = DbEntityTable.entityType
/**
 * DbEntityTable.touchedInLoading synonym
 */
val EntityTouchedCol = DbEntityTable.touchedInLoading
/**
 * DbEntityTable.previousStatus synonym
 */
val EntityPStatusCol = DbEntityTable.previousStatus
/**
 * DbEntityTable.currentStatus synonym
 */
val EntityCStatusCol = DbEntityTable.currentStatus
/**
 * DbEntityTable.created synonym
 */
val EntityCreatedCol = DbEntityTable.created
/**
 * DbEntityTable.updated synonym
 */
val EntityUpdatedCol = DbEntityTable.updated
/**
 * DbEntityTable.removed synonym
 */
val EntityRemovedCol = DbEntityTable.removed
/**
 * DbEntityTable.absentDays synonym
 */
val EntityAbsentCol = DbEntityTable.absentDays
