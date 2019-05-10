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

/**
 * Entity type column type
 *
 * @property entityTable DbEntityTable The entity table
 * @constructor
 */
class EntityTypeColumnType(private val entityTable: DbEntityTable) : ColumnType() {
    /**
     * Get SQL type for data
     *
     * @return String
     */
    override fun sqlType(): String {
        return VarCharColumnType(ENTITY_TYPE_NAME_LENGTH).sqlType()
    }

    /**
     * Convert value from DB
     *
     * @param value Any The DB value
     * @return Any
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is EntityType -> value
            is String -> entityTable.entityTypeManager.getEntityType(value)
                ?: throw AppEntityTypeException("Undefined entity type<$value>")
            else -> throw AppDatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }

    /**
     * Convert value to DB value
     *
     * @param value Any?
     * @return Any?
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> if (nullable) null else throw AppDataException("Null in non-nullable column")
            is EntityType -> value.name
            is String -> value
            else -> throw AppDatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }
}

/**
 * Register entity type column
 *
 * @receiver Table
 * @param name String The column name
 * @param entityTable DbEntityTable The exposed table
 * @return Column<EntityType>
 */
fun Table.entityType(name: String, entityTable: DbEntityTable): Column<EntityType> =
    registerColumn(name, EntityTypeColumnType(entityTable))
