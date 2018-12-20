package net.pototskiy.apps.magemediation.database.attribute

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column

abstract class TextAttribute(name: String) : TypedAttribute(name) {
    abstract val value: Column<String>
}

abstract class TextAttributeEntity(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    abstract var value: String

    override fun compareTo(other: Any): Int {
        other as String
        return value.compareTo(other)
    }

    override fun setValue(value: Any) {
        this.value = value as String
    }
}

abstract class TextAttributeEntityClass<out E : TextAttributeEntity>(
    table: TextAttribute,
    entityClass: Class<E>? = null
) : TypedAttributeEntityClass<String, E>(table, entityClass)