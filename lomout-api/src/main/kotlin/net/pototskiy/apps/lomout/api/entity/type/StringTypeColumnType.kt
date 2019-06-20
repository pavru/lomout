package net.pototskiy.apps.lomout.api.entity.type

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.PreparedStatement

/**
 * Column type for [STRING] values.
 *
 * @property columnType The exposed column type
 * @constructor
 */
class StringTypeColumnType(
    colLength: Int = 255,
    collate: String? = null,
    private val columnType: VarCharColumnType = VarCharColumnType(colLength, collate)
) : IColumnType by columnType {

    /**
     * Convert [STRING] to db value
     *
     * @param value
     * @return
     */
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is STRING -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert db value to [STRING]
     *
     * @param value
     * @return
     */
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is STRING -> value
            else -> STRING(columnType.valueFromDB(value) as String)
        }
    }

    /**
     * Convert [STRING] to db value
     *
     * @param value
     * @return
     */
    override fun valueToDB(value: Any?): Any? {
        return when (value) {
            null -> null
            is STRING -> columnType.notNullValueToDB(value.value)
            else -> columnType.notNullValueToDB(value)
        }
    }

    /**
     * Convert [STRING] to String
     *
     * @param value
     * @return
     */
    override fun nonNullValueToString(value: Any): String {
        return when (value) {
            is STRING -> columnType.nonNullValueToString(value.value)
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
            is STRING -> columnType.setParameter(stmt, index, value.value)
            else -> columnType.setParameter(stmt, index, value)
        }
    }

    /**
     * Convert [STRING] to String
     *
     * @param value
     * @return
     */
    override fun valueToString(value: Any?): String {
        return when (value) {
            is STRING -> columnType.valueToString(value.value)
            else -> columnType.valueToString(value)
        }
    }
}
