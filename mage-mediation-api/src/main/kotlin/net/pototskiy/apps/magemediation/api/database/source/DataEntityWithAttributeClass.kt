package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.source.SourceFieldType
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

abstract class DataEntityWithAttributeClass<out E : IntEntity>(
    table: IntIdTable,
    entityClass: Class<E>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : IntEntityClass<E>(table, entityClass) {

    @Suppress("MemberVisibilityCanBePrivate")
    protected val attributes: List<TypedAttributeEntityClass<*, *>> = attrEntityClass.toList()

    fun getAttrEntityClassFor(type: SourceFieldType): TypedAttributeEntityClass<*, *>? = transaction {
        when (type) {
            SourceFieldType.BOOL,
            SourceFieldType.BOOL_LIST -> findBoolAttrEntityClass()
            SourceFieldType.INT,
            SourceFieldType.INT_LIST -> findIntAttrEntityClass()
            SourceFieldType.DOUBLE,
            SourceFieldType.DOUBLE_LIST -> findDoubleAttrEntityClass()
            SourceFieldType.STRING,
            SourceFieldType.STRING_LIST -> findStringAttrEntityClass()
            SourceFieldType.TEXT -> findTextAttrEntityClass()
            SourceFieldType.DATE,
            SourceFieldType.DATE_LIST -> findDateAttrEntityClass()
            SourceFieldType.DATETIME,
            SourceFieldType.DATETIME_LIST -> findDateTimeAttrEntityClass()
            SourceFieldType.ATTRIBUTE_LIST ->
                throw DatabaseException("Attribute list can not have special store")
        }
    }

    private fun findDateTimeAttrEntityClass(): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumn = attrTable.value
            attr.valueType.isInstance(DateTime()) && attr.table.columns.any {
                it.name == valueColumn.name && it.columnType.sqlType() == DateColumnType(true).sqlType()
            }
        }
    }

    private fun findDateAttrEntityClass(): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumn = attrTable.value
            attr.valueType.isInstance(DateTime()) && attr.table.columns.any {
                it.name == valueColumn.name && it.columnType.sqlType() == DateColumnType(false).sqlType()
            }
        }
    }

    private fun findTextAttrEntityClass(): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumn = attrTable.value
            attr.valueType.isInstance("") && attr.table.columns.any {
                it.name == valueColumn.name && it.columnType.sqlType() == TextColumnType().sqlType()
            }
        }
    }

    private fun findStringAttrEntityClass(): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumn = attrTable.value
            attr.valueType.isInstance("") && attr.table.columns.any {
                it.name == valueColumn.name
                        && it.columnType.sqlType().replace(Regex("\\(.+\\)"), "") ==
                        VarCharColumnType().sqlType().replace(Regex("\\(.+\\)"), "")
            }
        }
    }

    private fun findDoubleAttrEntityClass(): TypedAttributeEntityClass<*, *>? =
        attributes.findLast { it.valueType.isInstance(0.0) }

    private fun findIntAttrEntityClass(): TypedAttributeEntityClass<*, *>? =
        attributes.findLast { it.valueType.isInstance(1L) }

    private fun findBoolAttrEntityClass(): TypedAttributeEntityClass<*, *>? =
        attributes.findLast { it.valueType.isInstance(true) }

}