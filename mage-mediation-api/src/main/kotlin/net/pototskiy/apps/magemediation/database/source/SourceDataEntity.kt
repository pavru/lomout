package net.pototskiy.apps.magemediation.database.source

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

abstract class SourceDataEntity(id: EntityID<Int>) : IntEntity(id) {
    abstract var createdInMedium: DateTime
    abstract var updatedInMedium: DateTime
    abstract var absentDays: Int

    abstract fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean
    abstract fun updateMainRecord(data: Map<String, Any?>)

    fun setUpdateDatetime(date: DateTime) = transaction {
        updatedInMedium = date
    }
}