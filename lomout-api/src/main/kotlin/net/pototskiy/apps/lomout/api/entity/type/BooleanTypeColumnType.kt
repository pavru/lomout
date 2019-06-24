package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.IColumnType
import java.sql.PreparedStatement

/**
 * Column type for [BOOLEAN] attribute value.
 *
 * @property columnType The exposed column type
 * @constructor
 */
class BooleanTypeColumnType(
    private val columnType: BooleanColumnType = BooleanColumnType()
) : IColumnType by columnType {

    /**
     * Convert non null value to db value.
     *
     * @param value
     * @return
     */
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is BOOLEAN -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert value from the DB to [BOOLEAN].
     *
     * @param value Any
     * @return Any
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is BOOLEAN -> value
            else -> BOOLEAN(columnType.valueFromDB(value))
        }
    }

    /**
     * Convert [BOOLEAN] to db value.
     *
     * @param value
     * @return
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is BOOLEAN -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert [BOOLEAN] to string.
     *
     * @param value
     * @return
     */
    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is BOOLEAN -> columnType.nonNullValueToString(value.value)
            else -> columnType.nonNullValueToString(value)
        }
    }

    /**
     * Convert [BOOLEAN] to String.
     *
     * @param value
     * @return
     */
    override fun valueToString(value: Any?): String {
        return when (value) {
            is BOOLEAN -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
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
            is BOOLEAN -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }
}
