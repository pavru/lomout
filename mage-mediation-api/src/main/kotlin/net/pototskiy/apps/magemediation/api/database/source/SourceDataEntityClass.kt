package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.TIMESTAMP
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.Duration

abstract class SourceDataEntityClass<out E : SourceDataEntity<*>>(
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

    fun insertNewRecord(data: Map<Attribute, Any>): E {
        return transaction {
            val entity =
                new {
                    touchedInLoading = true
                    previousStatus = SourceDataStatus.CREATED.name
                    currentStatus = SourceDataStatus.CREATED.name
                    createdInMedium = TIMESTAMP
                    updatedInMedium = TIMESTAMP
                    absentDays = 0
                }
            data.forEach { attribute, value ->
                addAttribute(entity, attribute, value)
            }
            entity
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
