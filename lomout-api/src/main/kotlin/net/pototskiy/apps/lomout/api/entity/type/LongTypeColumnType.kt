package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.LongColumnType
import java.sql.PreparedStatement

/**
 * Column type for [LONG] values.
 *
 * @property columnType The exposed column type
 * @constructor
 */
class LongTypeColumnType(
    private val columnType: LongColumnType = LongColumnType()
) : IColumnType by columnType {
    /**
     * Convert [LONG] to db value
     *
     * @param value
     * @return
     */
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is LONG -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert db value to [LONG]
     *
     * @param value
     * @return
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is LONG -> value
            else -> LONG(columnType.valueFromDB(value) as Long)
        }
    }

    /**
     * Convert [LONG] to db value
     *
     * @param value
     * @return
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is LONG -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert [LONG] to String
     *
     * @param value
     * @return
     */
    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is LONG -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
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
            is LONG -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    /**
     * Convert [LONG] to String
     *
     * @param value
     * @return
     */
    override fun valueToString(value: Any?): String {
        return when (value) {
            is LONG -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
