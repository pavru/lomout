package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IColumnType
import java.sql.PreparedStatement

/**
 * Column type for [DOUBLE] values.
 *
 * @property columnType The exposed column type
 * @constructor
 */
class DoubleTypeColumnType(
    private val columnType: DoubleColumnType = DoubleColumnType()
) : IColumnType by columnType {
    /**
     * Convert [DOUBLE] to the DB value
     *
     * @param value
     * @return
     */
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is DOUBLE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert db value to [DOUBLE]
     * @param value
     * @return
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is DOUBLE -> value
            else -> DOUBLE(columnType.valueFromDB(value) as Double)
        }
    }

    /**
     * Convert [DOUBLE] to db value
     *
     * @param value
     * @return
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is DOUBLE -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert [DOUBLE] to String
     *
     * @param value
     * @return
     */
    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is DOUBLE -> columnType.nonNullValueToString(value.value)
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
            is DOUBLE -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    /**
     * Convert [DOUBLE] to String
     *
     * @param value
     * @return
     */
    override fun valueToString(value: Any?): String {
        return when (value) {
            is DOUBLE -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
