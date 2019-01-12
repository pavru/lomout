package net.pototskiy.apps.magemediation.api.source

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import org.jetbrains.exposed.sql.*

enum class SourceFieldType(@Suppress("unused") val isList: Boolean = false) {
    BOOL,
    INT,
    DOUBLE,
    STRING,
    TEXT,
    DATE,
    DATETIME,
    BOOL_LIST(true),
    INT_LIST(true),
    DOUBLE_LIST(true),
    STRING_LIST(true),
    DATE_LIST(true),
    DATETIME_LIST(true),
    ATTRIBUTE_LIST(true)
}

fun SourceFieldType.isColumnTypeCompatible(type: IColumnType): Boolean {
    return when (this) {
        SourceFieldType.BOOL -> type is BooleanColumnType
        SourceFieldType.INT -> type is LongColumnType
        SourceFieldType.DOUBLE -> type is DoubleColumnType
        SourceFieldType.STRING -> type is VarCharColumnType
        SourceFieldType.TEXT -> type is TextColumnType
        SourceFieldType.DATE -> type is DateColumnType
        SourceFieldType.DATETIME -> type is DateColumnType && type.time
        SourceFieldType.BOOL_LIST -> type is BooleanColumnType
        SourceFieldType.INT_LIST -> type is LongColumnType
        SourceFieldType.DOUBLE_LIST -> type is DoubleColumnType
        SourceFieldType.STRING_LIST -> type is VarCharColumnType
        SourceFieldType.DATE_LIST -> type is DateColumnType
        SourceFieldType.DATETIME_LIST -> type is DateColumnType && type.time
        SourceFieldType.ATTRIBUTE_LIST ->
            throw DatabaseException("${this.name} can not be used as attribute type")
    }
}
