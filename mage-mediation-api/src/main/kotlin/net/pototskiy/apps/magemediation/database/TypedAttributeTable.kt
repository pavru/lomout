package net.pototskiy.apps.magemediation.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

abstract class TypedAttributeTable<V: Comparable<V>>(table: String):IntIdTable(table) {
    abstract val owner: Column<EntityID<Int>>
    val index = integer("index")
    val code = varchar("code", 300)
    abstract val value: Column<V>
}