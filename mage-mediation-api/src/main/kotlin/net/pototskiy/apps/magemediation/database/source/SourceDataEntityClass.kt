package net.pototskiy.apps.magemediation.database.source

import net.pototskiy.apps.magemediation.TIMESTAMP
import net.pototskiy.apps.magemediation.database.DatabaseException
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.source.SourceFieldType
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.Duration

abstract class SourceDataEntityClass<out E : SourceDataEntity>(
    table: SourceDataTable,
    entityClass: Class<E>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : IntEntityClass<E>(table, entityClass) {

    private val commonColumns = arrayOf(
        table.id.name,
        table.touchedInLoading.name,
        table.previousStatus.name,
        table.currentStatus.name,
        table.createdInMedium.name,
        table.updatedInMedium.name,
        table.removedInMedium.name,
        table.absentDays.name
    )
    private val attributes: List<TypedAttributeEntityClass<*, *>> = attrEntityClass.toList()

    val mainTableHeaders: List<Column<*>>
        get() {
            table as SourceDataTable
            return table.columns.filter { it.name !in commonColumns }
        }

    fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity? = transaction {
        find { this.keyWhereExpression(data) }.firstOrNull()
    }

    abstract fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean>

    fun insertNewRecord(data: Map<String, Any?>): SourceDataEntity {
        return transaction {
            new {
                touchedInLoading = true
                previousStatus = SourceDataStatus.CREATED.name
                currentStatus = SourceDataStatus.CREATED.name
                createdInMedium = TIMESTAMP
                updatedInMedium = TIMESTAMP
                absentDays = 0
                setEntityData(data)
            }
        }
    }

    fun resetTouchFlag() {
        transaction {
            table.update {
                this as SourceDataTable
                it[touchedInLoading] = false
            }
        }
    }

    fun markEntitiesAsRemove() {
        table as SourceDataTable
        transaction {
            table.update({ table.touchedInLoading eq false }) {
                it.update(previousStatus, currentStatus)
                it[currentStatus] = SourceDataStatus.REMOVED.name
                it[removedInMedium] = TIMESTAMP
            }
        }
    }

    fun removeOldEntities(maxAge: Int) {
        table as SourceDataTable
        transaction {
            find {
                ((table.absentDays greaterEq maxAge)
                        and (table.currentStatus eq SourceDataStatus.REMOVED.name))
            }.toList()
        }.forEach {
            transaction { it.delete() }
        }
    }

    fun updateAbsentAge() {
        table as SourceDataTable
        transaction {
            find { table.currentStatus eq SourceDataStatus.REMOVED.name }.toList()
        }.forEach {
            val days = Duration(it.updatedInMedium, TIMESTAMP).standardDays.toInt()
            transaction { it.absentDays = days }
        }
    }

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