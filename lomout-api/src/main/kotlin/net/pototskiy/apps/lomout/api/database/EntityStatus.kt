package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.AppDatabaseException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType

/**
 * Entity status
 */
enum class EntityStatus {
    /**
     * Entity created
     */
    CREATED,
    /**
     * Entity updated
     */
    UPDATED,
    /**
     * Entity removed
     */
    REMOVED,
    /**
     * Entity unchanged
     */
    UNCHANGED
}

private const val STATUS_NAME_LENGTH = 10

/**
 * Entity status SQL column type
 */
class EntityStatusColumnType : ColumnType() {
    /**
     * Get SQL column type
     *
     * @return String
     */
    override fun sqlType(): String {
        return VarCharColumnType(STATUS_NAME_LENGTH).sqlType()
    }

    /**
     * Convert value that is read from DB
     *
     * @param value Any
     * @return Any
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is EntityStatus -> value
            is String -> EntityStatus.valueOf(value)
            else -> throw AppDatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }

    /**
     * Convert value to write to DB
     *
     * @param value Any?
     * @return Any?
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> if (nullable) null else throw AppDatabaseException("Null in non-nullable column")
            is EntityStatus -> value.name
            is String -> value
            else -> throw AppDatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }
}

/**
 * Create and register entity status column
 *
 * @receiver Table
 * @param name String The column name
 * @return Column<EntityStatus>
 */
fun Table.entityStatus(name: String): Column<EntityStatus> =
    registerColumn(name, EntityStatusColumnType())
