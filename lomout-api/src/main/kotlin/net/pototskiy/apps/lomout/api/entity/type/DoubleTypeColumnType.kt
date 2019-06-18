package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IColumnType
import java.sql.PreparedStatement

class DoubleTypeColumnType(
    private val columnType: DoubleColumnType = DoubleColumnType()
) : IColumnType by columnType {

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is DOUBLE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is DOUBLE -> value
            else -> DOUBLE(columnType.valueFromDB(value) as Double)
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is DOUBLE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is DOUBLE -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is DOUBLE -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    override fun valueToString(value: Any?): String {
        return when (value) {
            is DOUBLE -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
