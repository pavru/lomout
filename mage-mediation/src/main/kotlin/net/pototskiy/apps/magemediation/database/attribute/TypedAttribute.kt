package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

abstract class TypedAttribute(name: String) : IntIdTable(name) {
    abstract val product: Column<EntityID<Int>>
    val index = integer("index")
    val code = varchar("code", 300)
}

abstract class TypedAttributeEntity<T>(id: EntityID<Int>) : IntEntity(id) {
    abstract var product: VersionEntity
    abstract var index: Int
    abstract var code: String

    abstract fun compareTo(other: Any): Int
    abstract fun setValue(value: Any)
}

abstract class TypedAttributeEntityClass<V, out E : TypedAttributeEntity<V>>(
    table: TypedAttribute,
    entityType: Class<E>? = null
) : IntEntityClass<E>(table, entityType)
