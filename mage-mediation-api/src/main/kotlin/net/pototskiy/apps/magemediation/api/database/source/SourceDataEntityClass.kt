package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.TIMESTAMP
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.source.SourceFieldType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.Duration

abstract class SourceDataEntityClass<out E : SourceDataEntity>(
    table: SourceDataTable,
    entityClass: Class<E>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : DataEntityWithAttributeClass<E>(table, entityClass, *attrEntityClass) {

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

    fun findByAttribute(attribute: String, type: SourceFieldType, value: Any): List<E> {
        return if (mainTableHeaders.any { it.name == attribute }) {
            findByAttributeInMainTable(attribute, value)
        } else {
            findByAttributeInAttrTable(attribute, type, value)
        }
    }

    private fun findByAttributeInAttrTable(attribute: String, type: SourceFieldType, value: Any): List<E> {
        val attrClass = getAttrEntityClassFor(type)
        val attrTable = attrClass?.table as? TypedAttributeTable<*>
            ?: throw DatabaseException("Source data entity has no attributes with type<${type.name}>")
        return transaction {
            (table innerJoin attrTable)
                .slice(table.columns)
                .select {
                    ((attrTable.code eq attribute) and equalExpression(attrTable.value, value))
                }
                .groupBy(*table.columns.toTypedArray())
                .map { wrapRow(it) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun SqlExpressionBuilder.equalExpression(c: Column<*>, value: Any): Op<Boolean> {
        return when {
            c.columnType is VarCharColumnType -> (c as Column<String>) eq value.toString()
            c.columnType is LongColumnType -> (c as Column<Long>) eq value.castToLong()
            c.columnType is DoubleColumnType -> (c as Column<Double>) eq value.castToDouble()
            c.columnType is DateColumnType -> (c as Column<DateTime>) eq value.castToDateTime()
            c.columnType is BooleanColumnType -> (c as Column<Boolean>) eq value.castToBoolean()
            c.columnType is TextColumnType -> (c as Column<String>) eq value.toString()
            else -> throw DatabaseException("Column and value types are incompatible therefore equal expression can not be built")
        }
    }

    private fun findByAttributeInMainTable(attribute: String, value: Any): List<E> {
        return transaction {
            val column = table.columns.findLast { it.name == attribute }!!
            table.select {
                equalExpression(column, value)
            }
                .groupBy(*table.columns.toTypedArray())
                .map { wrapRow(it) }
        }
    }

    fun getAttribute(entity: SourceDataEntity, attribute: String, type: SourceFieldType): List<Any> {
        return if (table.columns.any { it.name == attribute }) {
            getAttributeFromMainTable(entity, attribute)
        } else {
            getAttributeFromAttrTable(entity, attribute, type)
        }
    }

    private fun getAttributeFromMainTable(
        entity: SourceDataEntity,
        attribute: String
    ): List<Any> {
        val column = table.columns.findLast { it.name == attribute }!!
        return transaction {
            table
                .slice(column)
                .select { table.id eq entity.id }
                .map { it[column] }
                .filterNotNull()
                .toList()
        }
    }

    private fun getAttributeFromAttrTable(
        entity: SourceDataEntity,
        attribute: String,
        type: SourceFieldType
    ): List<Any> {
        val attrClass = getAttrEntityClassFor(type)
        val attrTable = attrClass?.table as? TypedAttributeTable<*>
            ?: throw DatabaseException("Source data entity has no attributes with type<${type.name}>")
        return transaction {
            (table innerJoin attrTable)
                .slice(attrTable.value)
                .select {
                    ((attrTable.owner eq entity.id) and (attrTable.code eq attribute))
                }
                .map { it[attrTable.value] }
                .toList()
        }
    }
}
