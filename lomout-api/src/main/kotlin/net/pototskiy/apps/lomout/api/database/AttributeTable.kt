package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.TextColumnType

/**
 * Exposed abstract attribute table
 *
 * @param V The type of attribute
 * @constructor
 * @param table The table name
 * @param owner The table of attributes owner
 * @param valueColumnType The SQL value column type
 */
abstract class AttributeTable<V : Comparable<V>>(
    table: String,
    owner: IntIdTable,
    valueColumnType: ColumnType
) : IntIdTable(table) {
    /**
     * The attribute owner
     */
    val owner = reference("owner", owner, ReferenceOption.CASCADE)
    /**
     * The attribute value index in list type, -1 not list type value
     */
    val index = integer("index")
    /**
     * Attribute code (name)
     */
    val code = varchar("code", codeNameLength).index()
    /**
     * Attribute value
     */
    val value: Column<V> = registerColumn("value", valueColumnType)

    init {
        if (value.columnType !is TextColumnType) {
            value.index()
        }
        @Suppress("LeakingThis")
        uniqueIndex("unique_attr", this.owner, code, index)
    }

    /**
     * Companion name
     */
    companion object {
        /**
         * Maximum length of attribute code (name)
         */
        const val codeNameLength = 300
    }
}
