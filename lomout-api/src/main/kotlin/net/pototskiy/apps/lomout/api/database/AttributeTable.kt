package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.entity.type.Type
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Exposed abstract attribute table
 *
 * @param V The type of attribute
 * @constructor
 * @param table The table name
 * @param owner The table of attributes owner
 * @param valueColumnType The SQL value column type
 */
internal abstract class AttributeTable<V : Type>(
    table: String,
    owner: IntIdTable,
    valueColumnType: IColumnType
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
    val code = varchar("code", CODE_NAME_LENGTH)
    /**
     * Attribute value
     */
    val value: Column<Type> = registerColumn("value", valueColumnType)

    init {
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
        const val CODE_NAME_LENGTH = 70
    }
}
