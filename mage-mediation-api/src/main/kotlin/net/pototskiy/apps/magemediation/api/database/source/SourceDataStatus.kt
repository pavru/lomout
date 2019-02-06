package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

enum class SourceDataStatus {
    CREATED, UPDATED, REMOVED, UNCHANGED
}

class SourceDataStatusColumnType : ColumnType() {
    override fun sqlType(): String = "VARCHAR(10)"

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is SourceDataStatus -> value
            is String -> SourceDataStatus.valueOf(value)
            else -> throw DatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when(value) {
            null -> if (nullable) null else throw DatabaseException("Null in non-nullable column")
            is SourceDataStatus -> value.name
            is String -> value
            else -> throw DatabaseException("Unexpected value: $value of ${value::class.qualifiedName}")
        }
    }
}

fun Table.sourceDataStatus(name: String): Column<SourceDataStatus> =
    registerColumn(name, SourceDataStatusColumnType())
