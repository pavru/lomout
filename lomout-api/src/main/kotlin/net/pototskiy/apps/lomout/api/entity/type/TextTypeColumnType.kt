package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.TextColumnType
import java.sql.PreparedStatement

class TextTypeColumnType(
    collate: String? = null,
    private val columnType: TextColumnType = TextColumnType(collate)
) : IColumnType by columnType {

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is TEXT -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is TEXT -> value
            else -> TEXT(columnType.valueFromDB(value) as String)
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is TEXT -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is TEXT -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is TEXT -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    override fun valueToString(value: Any?): String {
        return when (value) {
            is TEXT -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
