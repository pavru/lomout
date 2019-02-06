package net.pototskiy.apps.magemediation.api.database.newschema

import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataState
import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataTarget
import net.pototskiy.apps.magemediation.api.database.source.PersistentEntityClass
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
