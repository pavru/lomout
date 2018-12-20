package net.pototskiy.apps.magemediation.database.attribute

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column

abstract class BoolAttribute(name: String) : TypedAttribute(name) {
    abstract val value: Column<Boolean>
}

abstract class BoolAttributeEntity(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    abstract var value: Boolean

    override fun compareTo(other: Any): Int {
        other as Boolean
        return if (value == other) 0 else 1
    }

    override fun setValue(value: Any) {
        this.value = value as Boolean
    }
}

abstract class BoolAttributeEntityClass<out E : BoolAttributeEntity>(
    table: BoolAttribute,
    entityClass: Class<E>? = null
) : TypedAttributeEntityClass<Boolean, E>(table, entityClass)