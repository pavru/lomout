package net.pototskiy.apps.magemediation.database.attribute

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import kotlin.math.abs

abstract class DoubleAttribute(name: String) : TypedAttribute(name) {
    abstract val value: Column<Double>
}

abstract class DoubleAttributeEntity(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    abstract var value: Double
    override fun compareTo(other: Any): Int {
        other as Double
        val diff = value - other
        return if (diff == 0.0) 0 else (diff/abs(diff)).toInt()
    }

    override fun setValue(value: Any) {
        this.value = value as Double
    }
}

abstract class DoubleAttributeEntityClass<out E : DoubleAttributeEntity>(
    table: DoubleAttribute,
    entityClass: Class<E>? = null
) : TypedAttributeEntityClass<Double, E>(table, entityClass)