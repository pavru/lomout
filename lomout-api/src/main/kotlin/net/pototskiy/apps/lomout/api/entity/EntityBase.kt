package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.TIMESTAMP
import net.pototskiy.apps.lomout.api.database.EntityStatus
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

abstract class EntityBase internal constructor(
    val type: EntityType,
    val id: EntityID<Int>,
    protected val repository: EntityRepositoryInterface
) {
    var touchedInLoading: Boolean = true
    var currentStatus: EntityStatus = EntityStatus.CREATED
    var previousStatus: EntityStatus? = EntityStatus.CREATED
    var created: DateTime = TIMESTAMP
    var updated: DateTime = TIMESTAMP
    var removed: DateTime? = null
    var absentDays: Int = 0
    /**
     * Update entity create status
     */
    fun wasCreated() {
        touchedInLoading = true
        previousStatus = EntityStatus.CREATED
        currentStatus = EntityStatus.CREATED
        updated = TIMESTAMP
        absentDays = 0
    }

    /**
     * Update entity update status
     *
     * @param onlyCurrent Boolean true — update only current status, false — update current and previous status
     */
    fun wasUpdated(onlyCurrent: Boolean = false) {
        touchedInLoading = true
        previousStatus = if (onlyCurrent) {
            previousStatus
        } else {
            currentStatus
        }
        currentStatus = EntityStatus.UPDATED
        updated = TIMESTAMP
        absentDays = 0
    }

    /**
     * Update entity unchanged status
     *
     * @param onlyCurrent Boolean true — update only current status, false — update current and previous status
     */
    fun wasUnchanged(onlyCurrent: Boolean = false) {
        touchedInLoading = true
        previousStatus = if (onlyCurrent) {
            previousStatus
        } else {
            currentStatus
        }
        currentStatus = EntityStatus.UNCHANGED
        updated = TIMESTAMP
    }
}
