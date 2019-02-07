package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import org.jetbrains.exposed.sql.transactions.transaction

abstract class PersistentMediumEntityClass(
    table: PersistentMediumEntityTable,
    entityClass: Class<PersistentMediumEntity>? = null,
    vararg attrEntityClass: AttributeEntityClass<*, *>
) : PersistentEntityClass<PersistentMediumEntity>(table, entityClass, *attrEntityClass) {

    fun insertNewRecord(
        dataTarget: MediumDataTarget,
        dataState: MediumDataState,
        data: Map<Attribute, Any>
    ): PersistentMediumEntity {
        return transaction {
            val entity =
                new {
                    target = dataTarget
                    state = dataState
                }
            data.forEach { attribute, value ->
                addAttribute(entity, attribute, value)
            }
            entity
        }
    }
}
