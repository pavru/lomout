package net.pototskiy.apps.magemediation.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

abstract class VersionTable(table: String) : IntIdTable(table) {
    val createdInMedium = datetime("created_in_medium").index()
    val updatedInMedium = datetime("updated_in_medium").index()
    val absentDays = integer("absent_days").index()

}

abstract class VersionEntity(id: EntityID<Int>) : IntEntity(id) {
    abstract var createdInMedium: DateTime
    abstract var updatedInMedium: DateTime
    abstract var absentDays: Int

    abstract fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean
    abstract fun updateMainRecord(data: Map<String, Any?>)

    fun setUpdateDatetime(date: DateTime) = transaction {
        updatedInMedium = date
    }
}

abstract class VersionEntityClass<out E : VersionEntity>(table: VersionTable, entityType: Class<E>? = null) :
    IntEntityClass<E>(table, entityType) {

    abstract fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity?
    abstract fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity
}
