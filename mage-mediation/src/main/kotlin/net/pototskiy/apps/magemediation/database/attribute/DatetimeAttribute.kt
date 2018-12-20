package net.pototskiy.apps.magemediation.database.attribute

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.math.abs

abstract class DatetimeAttribute(name: String) : TypedAttribute(name) {
    abstract val value: Column<DateTime>
}

abstract class DatetimeAttributeEntity(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    abstract var value: DateTime

    override fun compareTo(other: Any): Int {
        other as DateTime
        val diff = Duration(value, other).millis
        return if (diff == 0L) 0 else (diff/abs(diff)).toInt()
    }

    override fun setValue(value: Any) {
        this.value = value as DateTime
    }
}

abstract class DatetimeAttributeEntityClass<out E : DatetimeAttributeEntity>(
    table: DatetimeAttribute,
    entityClass: Class<E>? = null
) : TypedAttributeEntityClass<DateTime, E>(table, entityClass)