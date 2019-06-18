package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.joda.time.DateTime
import java.sql.PreparedStatement

class DateTypeColumnType(
    private val columnType: DateColumnType = DateColumnType(false)
) : IColumnType by columnType {

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is DATE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is DATE -> value
            else -> DATE(columnType.valueFromDB(value) as DateTime)
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is DATE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is DATE -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    override fun valueToString(value: Any?): String {
        return when (value) {
            is DATE -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is DATE -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }
}
