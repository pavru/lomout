package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.TIMESTAMP
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.Duration

abstract class PersistentSourceEntityClass(
    table: PersistentSourceEntityTable,
    entityClass: Class<PersistentSourceEntity>? = null,
    vararg attrEntityClass: AttributeEntityClass<*, *>
) : PersistentEntityClass<PersistentSourceEntity>(table, entityClass, *attrEntityClass) {

    fun insertNewRecord(entityClass: EntityClass<*>, data: Map<Attribute, Any>): PersistentSourceEntity {
        return transaction {
            val entity =
                new {
                    entityType = entityClass.type
                    touchedInLoading = true
                    previousStatus = SourceDataStatus.CREATED
                    currentStatus = SourceDataStatus.CREATED
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

    fun resetTouchFlag(entityClass: EntityClass<*>) {
        transaction {
            table.update({ getClassWhereClause(entityClass) }) {
                this as PersistentSourceEntityTable
                it[touchedInLoading] = false
            }
        }
    }

    private fun getClassWhereClause(entityClass: EntityClass<*>): Op<Boolean> = Op.build {
        table as PersistentSourceEntityTable
        table.entityType eq entityClass.type
    }

    fun markEntitiesAsRemove(entityClass: EntityClass<*>) {
        table as PersistentSourceEntityTable
        transaction {
            table.update({
                getClassWhereClause(entityClass)
                    .and(table.touchedInLoading eq false)
                    .and(table.currentStatus neq SourceDataStatus.REMOVED)
            }
            ) {
                it.update(previousStatus, currentStatus)
                it[currentStatus] = SourceDataStatus.REMOVED
                it[removedInMedium] = TIMESTAMP
            }
        }
    }

    fun removeOldEntities(entityClass: EntityClass<*>, maxAge: Int) {
        table as PersistentSourceEntityTable
        transaction {
            find {
                getClassWhereClause(entityClass).and(
                    (table.absentDays greaterEq maxAge)
                            and (table.currentStatus eq SourceDataStatus.REMOVED)
                )
            }.toList()
        }.forEach {
            transaction { it.delete() }
        }
    }

    fun updateAbsentAge(entityClass: EntityClass<*>) {
        table as PersistentSourceEntityTable
        transaction {
            find {
                getClassWhereClause(entityClass).and(table.currentStatus eq SourceDataStatus.REMOVED)
            }.toList()
        }.forEach {
            val days = Duration(it.removedInMedium, TIMESTAMP).standardDays.toInt()
            transaction { it.absentDays = days }
        }
    }
}
