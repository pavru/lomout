package net.pototskiy.apps.magemediation.api.database

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

abstract class TypedAttributeTable<V : Comparable<V>>(
    table: String,
    owner: IntIdTable,
    valueColumn: Table.()->Column<V>
) : IntIdTable(table) {
    val owner = reference("owner", owner, ReferenceOption.CASCADE)
    val index = integer("index")
    val code = varchar("code", 300)
    val value: Column<V> = this.valueColumn()
}