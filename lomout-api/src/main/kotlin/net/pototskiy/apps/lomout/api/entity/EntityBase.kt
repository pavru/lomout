package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.TIMESTAMP
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime

/**
 * Entity base class
 *
 * @property type The entity type
 * @property id The entity id
 * @property repository The entity repository
 * @property touchedInLoading Is entity touched?
 * @property currentStatus The entity current status
 * @property previousStatus The entity previous status
 * @property created The entity creation date
 * @property updated The entity updating date
 * @property removed The entity removing date
 * @property absentDays Days entity is absent
 * @constructor
 */
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
     * Update entity status to **created**
     */
    fun wasCreated() {
        touchedInLoading = true
        previousStatus = EntityStatus.CREATED
        currentStatus = EntityStatus.CREATED
        updated = TIMESTAMP
        absentDays = 0
    }

    /**
     * Update entity status to **updated**
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
     * Update entity status to **unchanged**
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
