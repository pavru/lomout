package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDatabaseException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Table

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
        return IntegerColumnType().sqlType()
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
            is Int -> EntityStatus.values()[value]
            else -> throw AppDatabaseException("Unexpected value '$value' of type '${value::class.qualifiedName}'.")
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
            null -> if (nullable) null else throw AppDatabaseException("Null in non-nullable column.")
            is EntityStatus -> value.ordinal
            is Int -> value
            else -> throw AppDatabaseException("Unexpected value '$value' of type '${value::class.qualifiedName}'.")
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
