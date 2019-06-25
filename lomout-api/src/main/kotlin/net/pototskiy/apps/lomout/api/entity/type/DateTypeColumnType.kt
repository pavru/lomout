package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.joda.time.DateTime
import java.sql.PreparedStatement

/**
 * Column type for [DATE] value.
 *
 * @property columnType The exposed column type
 * @constructor
 */
class DateTypeColumnType(
    private val columnType: DateColumnType = DateColumnType(false)
) : IColumnType by columnType {

    /**
     * Convert [DATE] to db value
     *
     * @param value
     * @return
     */
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is DATE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert db value to [DATE]
     *
     * @param value
     * @return
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is DATE -> value
            else -> DATE(columnType.valueFromDB(value) as DateTime)
        }
    }

    /**
     * Convert [DATE] to db value
     *
     * @param value
     * @return
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is DATE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert [DATE] to String
     *
     * @param value
     * @return
     */
    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is DATE -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    /**
     * Convert [DATE] to String
     *
     * @param value
     * @return
     */
    override fun valueToString(value: Any?): String {
        return when (value) {
            is DATE -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }

    /**
     * Set statement parameters
     *
     * @param stmt PreparedStatement
     * @param index Int
     * @param value Any?
     */
    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is DATE -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }
}
