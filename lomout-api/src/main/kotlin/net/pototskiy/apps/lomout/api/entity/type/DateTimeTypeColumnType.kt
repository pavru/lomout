package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.joda.time.DateTime
import java.sql.PreparedStatement

class DateTimeTypeColumnType(
    private val columnType: DateColumnType = DateColumnType(true)
) : IColumnType by columnType {

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is DATETIME -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is DATETIME -> value
            else -> DATETIME(columnType.valueFromDB(value) as DateTime)
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is DATETIME -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is DATETIME -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is DATETIME -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    override fun valueToString(value: Any?): String {
        return when (value) {
            is DATETIME -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
