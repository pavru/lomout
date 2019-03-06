package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.ENTITY_TYPE_NAME_LENGTH
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType

class EntityTypeColumnType : ColumnType() {
    override fun sqlType(): String {
        return VarCharColumnType(ENTITY_TYPE_NAME_LENGTH).sqlType()
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is EntityType -> value
            is String -> EntityTypeManager[value]
            else -> throw DatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> if (nullable) null else throw DatabaseException("Null in non-nullable column")
            is EntityType -> value.name
            is String -> value
            else -> throw DatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }
}

fun Table.entityType(name: String): Column<EntityType> =
    registerColumn(name, EntityTypeColumnType())
