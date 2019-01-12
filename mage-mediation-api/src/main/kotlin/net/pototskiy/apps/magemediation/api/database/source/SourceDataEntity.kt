package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.TIMESTAMP
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

abstract class SourceDataEntity(id: EntityID<Int>) : IntEntity(id) {
    var touchedInLoading: Boolean
        get() = (klass.table as SourceDataTable).touchedInLoading.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).touchedInLoading.setValue(this, ::touchedInLoading, value)
    var previousStatus: String
        get() = (klass.table as SourceDataTable).previousStatus.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).previousStatus.setValue(this, ::touchedInLoading, value)
    var currentStatus: String
        get() = (klass.table as SourceDataTable).currentStatus.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).currentStatus.setValue(this, ::touchedInLoading, value)
    var createdInMedium: DateTime
        get() = (klass.table as SourceDataTable).createdInMedium.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).createdInMedium.setValue(this, ::touchedInLoading, value)
    var updatedInMedium: DateTime
        get() = (klass.table as SourceDataTable).updatedInMedium.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).updatedInMedium.setValue(this, ::touchedInLoading, value)
    var removedInMedium: DateTime?
        get() = (klass.table as SourceDataTable).removedInMedium.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).removedInMedium.setValue(this, ::touchedInLoading, value)
    var absentDays: Int
        get() = (klass.table as SourceDataTable).absentDays.getValue(this, ::touchedInLoading)
        set(value) = (klass.table as SourceDataTable).absentDays.setValue(this, ::touchedInLoading, value)

    abstract fun isNotEqual(data: Map<String, Any?>): Boolean
    abstract fun updateEntity(data: Map<String, Any?>)

    abstract fun setEntityData(data: Map<String, Any?>)

    fun wasCreated() = transaction {
        touchedInLoading = true
        previousStatus = SourceDataStatus.CREATED.name
        currentStatus = SourceDataStatus.CREATED.name
        updatedInMedium = TIMESTAMP
        absentDays = 0
    }

    fun wasUpdated() = transaction {
        touchedInLoading = true
        previousStatus = currentStatus
        currentStatus = SourceDataStatus.UPDATED.name
        updatedInMedium = TIMESTAMP
        absentDays = 0
    }

    fun wasRemoved() = transaction {
        previousStatus = currentStatus
        currentStatus = SourceDataStatus.REMOVED.name
        removedInMedium = TIMESTAMP
    }

    fun wasUnchanged() = transaction {
        touchedInLoading = true
        previousStatus = currentStatus
        currentStatus = SourceDataStatus.UNCHANGED.name
        updatedInMedium = TIMESTAMP
    }

}