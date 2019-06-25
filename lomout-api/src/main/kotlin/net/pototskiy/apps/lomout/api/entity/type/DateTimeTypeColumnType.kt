package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.joda.time.DateTime
import java.sql.PreparedStatement

/**
 * Column type for [DATE] type.
 *
 * @property columnType The exposed column type
 * @constructor
 */
class DateTimeTypeColumnType(
    private val columnType: DateColumnType = DateColumnType(true)
) : IColumnType by columnType {

    /**
     * Convert [DATETIME] to db value.
     *
     * @param value
     * @return
     */
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is DATETIME -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert db value to [DATETIME].
     *
     * @param value
     * @return
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is DATETIME -> value
            else -> DATETIME(columnType.valueFromDB(value) as DateTime)
        }
    }

    /**
     * Convert [DATETIME] to the DB value.
     *
     * @param value
     * @return
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is DATETIME -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert [DATETIME] to String.
     *
     * @param value
     * @return
     */
    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is DATETIME -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    /**
     * Set statement parameters.
     *
     * @param stmt PreparedStatement
     * @param index Int
     * @param value Any?
     */
    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        when (value) {
            is DATETIME -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    /**
     * Convert [DATETIME] to String.
     *
     * @param value
     * @return
     */
    override fun valueToString(value: Any?): String {
        return when (value) {
            is DATETIME -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
