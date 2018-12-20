package net.pototskiy.apps.magemediation.database.attribute

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import kotlin.math.abs

abstract class IntAttribute(name: String) : TypedAttribute(name) {
    abstract val value: Column<Long>
}

abstract class IntAttributeEntity(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    abstract var value: Long

    override fun compareTo(other: Any): Int {
        other as Long
        val diff = value - other
        return if (diff == 0L) 0 else (diff / abs(diff)).toInt()
    }

    override fun setValue(value: Any) {
        this.value = value as Long
    }
}

abstract class IntAttributeEntityClass<out E : IntAttributeEntity>(
    table: IntAttribute,
    entityClass: Class<E>? = null
) : TypedAttributeEntityClass<Long, E>(table, entityClass)