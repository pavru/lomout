package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.TIMESTAMP
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.Type
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction

class DbEntity(id: EntityID<Int>) : IntEntity(id) {
    var entityType by DbEntityTable.entityType
    var touchedInLoading by DbEntityTable.touchedInLoading
    var previousStatus by DbEntityTable.previousStatus
    var currentStatus by DbEntityTable.currentStatus
    var created by DbEntityTable.created
    var updated by DbEntityTable.updated
    var removed by DbEntityTable.removed
    var absentDays by DbEntityTable.absentDays

    val eType by lazy {
        EntityTypeManager.getEntityType(entityType)
            ?: throw ConfigException("Entity type<$entityType> is not defined")
    }

    var data: MutableMap<AnyTypeAttribute, Type?> = mutableMapOf()

    operator fun get(attribute: String): Type? = data[eType.getAttributeOrNull(attribute)]

    fun wasCreated() = transaction {
        touchedInLoading = true
        previousStatus = EntityStatus.CREATED
        currentStatus = EntityStatus.CREATED
        updated = TIMESTAMP
        absentDays = 0
    }

    fun wasUpdated(onlyCurrent: Boolean = false) = transaction {
        touchedInLoading = true
        previousStatus = if (onlyCurrent) {
            previousStatus
        } else {
            currentStatus
        }
        currentStatus = EntityStatus.UPDATED
        updated = TIMESTAMP
        absentDays = 0
    }

    fun wasUnchanged(onlyCurrent: Boolean = false) = transaction {
        touchedInLoading = true
        previousStatus = if (onlyCurrent) {
            previousStatus
        } else {
            currentStatus
        }
        currentStatus = EntityStatus.UNCHANGED
        updated = TIMESTAMP
    }

    fun addAttribute(attribute: AnyTypeAttribute, value: Type) =
        addAttribute(this, attribute, value)

    fun updateAttribute(attribute: AnyTypeAttribute, value: Type) =
        updateAttribute(this, attribute, value)

    fun removeAttribute(attribute: AnyTypeAttribute) =
        removeAttribute(this, attribute)

    fun readAttribute(attribute: AnyTypeAttribute) =
        readAttribute(this, attribute)

    fun readAttributes() =
        readAttributes(this)

    companion object : DbEntityClass(
        EntityVarchar,
        EntityLong,
        EntityDouble,
        EntityBoolean,
        EntityDateTime,
        EntityText
    )
}
