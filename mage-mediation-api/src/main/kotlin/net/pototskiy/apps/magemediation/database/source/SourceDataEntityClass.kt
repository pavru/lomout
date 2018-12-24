package net.pototskiy.apps.magemediation.database.source

import org.jetbrains.exposed.dao.IntEntityClass
import org.joda.time.DateTime

abstract class SourceDataEntityClass<out E : SourceDataEntity>(
    table: SourceDataTable,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass) {

    abstract fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity?
    abstract fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): SourceDataEntity

}