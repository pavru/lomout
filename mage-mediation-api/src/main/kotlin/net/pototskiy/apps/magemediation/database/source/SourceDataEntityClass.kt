package net.pototskiy.apps.magemediation.database.source

import net.pototskiy.apps.magemediation.TIMESTAMP
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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
}
