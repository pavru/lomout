package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.TextColumnType

abstract class AttributeTable<V : Comparable<V>>(
    table: String,
    owner: IntIdTable,
    valueColumnType: ColumnType
) : IntIdTable(table) {
    val owner = reference("owner", owner, ReferenceOption.CASCADE)
    val index = integer("index")
    val code = varchar("code", codeNameLength).index()
    val value: Column<V> = registerColumn("value", valueColumnType)

    init {
        if (value.columnType !is TextColumnType) {
            value.index()
        }
        @Suppress("LeakingThis")
        uniqueIndex("unique_attr", this.owner, code, index)
    }

    companion object {
        const val codeNameLength = 300
    }
}
