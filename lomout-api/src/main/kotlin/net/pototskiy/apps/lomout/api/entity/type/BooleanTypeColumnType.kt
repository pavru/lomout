package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.IColumnType
import java.sql.PreparedStatement

class BooleanTypeColumnType(private val columnType: BooleanColumnType = BooleanColumnType()) :
    IColumnType by columnType {

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is BOOLEAN -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is BOOLEAN -> value
            else -> BOOLEAN(columnType.valueFromDB(value))
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is BOOLEAN -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is BOOLEAN -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    override fun valueToString(value: Any?): String {
        return when (value) {
            is BOOLEAN -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is BOOLEAN -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }
}
