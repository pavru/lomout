package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.AppDatabaseException
import net.pototskiy.apps.lomout.api.AppEntityTypeException
import net.pototskiy.apps.lomout.api.ENTITY_TYPE_NAME_LENGTH
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType

class EntityTypeColumnType(private val entityTable: DbEntityTable) : ColumnType() {
    override fun sqlType(): String {
        return VarCharColumnType(ENTITY_TYPE_NAME_LENGTH).sqlType()
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is EntityType -> value
            is String -> entityTable.entityTypeManager.getEntityType(value)
                ?: throw AppEntityTypeException("Undefined entity type<$value>")
            else -> throw AppDatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> if (nullable) null else throw AppDataException("Null in non-nullable column")
            is EntityType -> value.name
            is String -> value
            else -> throw AppDatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }
}

fun Table.entityType(name: String, entityTable: DbEntityTable): Column<EntityType> =
    registerColumn(name, EntityTypeColumnType(entityTable))
