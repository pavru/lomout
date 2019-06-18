package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.database.DbEntityTable
import org.jetbrains.exposed.sql.ResultRow

internal fun ResultRow.toEntity(repository: EntityRepositoryInterface): Entity {
    return Entity(this[DbEntityTable.entityType], this[DbEntityTable.id], repository).apply {
        touchedInLoading = this@toEntity[DbEntityTable.touchedInLoading]
        currentStatus = this@toEntity[DbEntityTable.currentStatus]
        previousStatus = this@toEntity[DbEntityTable.previousStatus]
        created = this@toEntity[DbEntityTable.created]
        updated = this@toEntity[DbEntityTable.updated]
        removed = this@toEntity[DbEntityTable.removed]
        absentDays = this@toEntity[DbEntityTable.absentDays]
    }
}
