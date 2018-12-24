package net.pototskiy.apps.magemediation.database.attribute

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

abstract class TypedAttributeTable<V : Comparable<V>>(name: String) : IntIdTable(name) {
    abstract val owner: Column<EntityID<Int>>
    val index = integer("index")
    val code = varchar("code", 300)
    abstract val value: Column<V>
}

abstract class TypedAttributeEntity<V : Comparable<V>>(id: EntityID<Int>) : IntEntity(id) {
    abstract var owner: IntEntity
    abstract var index: Int
    abstract var code: String
    abstract var value: V

    fun compareToWithTypeCheck(other: Any): Int {
        @Suppress("UNCHECKED_CAST")
        return value.compareTo(other as V)
    }

    fun setValueWithTypeCheck(value: Any) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as V
    }
}

abstract class TypedAttributeEntityClass<V : Comparable<V>, out E : TypedAttributeEntity<V>>(
    table: TypedAttributeTable<V>,
    entityType: Class<E>? = null
) : IntEntityClass<E>(table, entityType)
