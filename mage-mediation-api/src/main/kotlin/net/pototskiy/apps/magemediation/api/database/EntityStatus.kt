package net.pototskiy.apps.magemediation.api.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType

enum class EntityStatus {
    CREATED, UPDATED, REMOVED, UNCHANGED
}

class EntityStatusColumnType : ColumnType() {
    override fun sqlType(): String = VarCharColumnType(10).sqlType()

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is EntityStatus -> value
            is String -> EntityStatus.valueOf(value)
            else -> throw DatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> if (nullable) null else throw DatabaseException("Null in non-nullable column")
            is EntityStatus -> value.name
            is String -> value
            else -> throw DatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }
}

fun Table.entityStatus(name: String): Column<EntityStatus> =
    registerColumn(name, EntityStatusColumnType())
