package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.TIMESTAMP
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

open class PersistentSourceEntity(id: EntityID<Int>): PersistentEntity<PersistentSourceEntity>(id) {
    var touchedInLoading: Boolean
        get() = (klass.table as PersistentSourceEntityTable).touchedInLoading.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as PersistentSourceEntityTable).touchedInLoading.setValue(this, ::touchedInLoading, value)
    var previousStatus: SourceDataStatus?
        get() = (klass.table as PersistentSourceEntityTable).previousStatus.getValue(this, ::previousStatus)
        set(value) = (klass.table as PersistentSourceEntityTable).previousStatus.setValue(this, ::previousStatus, value)
    var currentStatus: SourceDataStatus
        get() = (klass.table as PersistentSourceEntityTable).currentStatus.getValue(this, ::currentStatus)
        set(value) = (klass.table as PersistentSourceEntityTable).currentStatus.setValue(this, ::currentStatus, value)
    var createdInMedium: DateTime
        get() = (klass.table as PersistentSourceEntityTable).createdInMedium.getValue(this, ::createdInMedium)
        set(value) = (klass.table as PersistentSourceEntityTable).createdInMedium.setValue(this, ::createdInMedium, value)
    var updatedInMedium: DateTime
        get() = (klass.table as PersistentSourceEntityTable).updatedInMedium.getValue(this, ::updatedInMedium)
        set(value) = (klass.table as PersistentSourceEntityTable).updatedInMedium.setValue(this, ::updatedInMedium, value)
    var removedInMedium: DateTime?
        get() = (klass.table as PersistentSourceEntityTable).removedInMedium.getValue(this, ::removedInMedium)
        set(value) = (klass.table as PersistentSourceEntityTable).removedInMedium.setValue(this, ::removedInMedium, value)
    var absentDays: Int
        get() = (klass.table as PersistentSourceEntityTable).absentDays.getValue(this, ::absentDays)
        set(value) = (klass.table as PersistentSourceEntityTable).absentDays.setValue(this, ::absentDays, value)

    fun wasCreated() = transaction {
        touchedInLoading = true
        previousStatus = SourceDataStatus.CREATED
        currentStatus = SourceDataStatus.CREATED
        updatedInMedium = TIMESTAMP
        absentDays = 0
    }

    fun wasUpdated() = transaction {
        touchedInLoading = true
        previousStatus = currentStatus
        currentStatus = SourceDataStatus.UPDATED
        updatedInMedium = TIMESTAMP
        absentDays = 0
    }

    @PublicApi
    fun wasRemoved() = transaction {
        previousStatus = currentStatus
        currentStatus = SourceDataStatus.REMOVED
        removedInMedium = TIMESTAMP
    }

    fun wasUnchanged() = transaction {
        touchedInLoading = true
        previousStatus = currentStatus
        currentStatus = SourceDataStatus.UNCHANGED
        updatedInMedium = TIMESTAMP
    }

}
