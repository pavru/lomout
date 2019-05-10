package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.TIMESTAMP
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Type
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Data entity DB object
 *
 * @property entityType EntityType
 * @property touchedInLoading Boolean
 * @property previousStatus EntityStatus?
 * @property currentStatus EntityStatus
 * @property created DateTime
 * @property updated DateTime
 * @property removed DateTime?
 * @property absentDays Int
 * @property eType EntityType
 * @property data MutableMap<Attribute<out Type>, Type?>
 * @constructor
 * @param id EntityID<Int> The internal entity id
 */
class DbEntity(id: EntityID<Int>) : IntEntity(id) {
    /**
     * Entity type
     */
    var entityType by DbEntityTable.entityType
    /**
     * Flag that entity changed in last processing
     */
    var touchedInLoading by DbEntityTable.touchedInLoading
    /**
     * Previous status of entity, before last processing
     */
    var previousStatus by DbEntityTable.previousStatus
    /**
     * Current status of entity, after last processing
     */
    var currentStatus by DbEntityTable.currentStatus
    /**
     * Timestamp when entity was created
     */
    var created by DbEntityTable.created
    /**
     * Timestamp when entity was update last time
     */
    var updated by DbEntityTable.updated
    /**
     * Timestamp when entity was remove in source data
     */
    var removed by DbEntityTable.removed
    /**
     * Number of days that entity is not in source data
     */
    var absentDays by DbEntityTable.absentDays

    /**
     * Entity type
     */
    val eType by lazy { entityType
//        EntityTypeManager.getEntityType(entityType)
//            ?: throw ConfigException("Entity type<$entityType> is not defined")
    }

    /**
     * Entity attribute values
     */
    var data: MutableMap<AnyTypeAttribute, Type?> = mutableMapOf()

    /**
     * Get entity attribute value
     *
     * @param attribute String
     * @return Type? The value of null if entity has no attribute
     */
    operator fun get(attribute: String): Type? = data[eType.getAttributeOrNull(attribute)]

    /**
     * Update entity create status
     */
    fun wasCreated() = transaction {
        touchedInLoading = true
        previousStatus = EntityStatus.CREATED
        currentStatus = EntityStatus.CREATED
        updated = TIMESTAMP
        absentDays = 0
    }

    /**
     * Update entity update status
     *
     * @param onlyCurrent Boolean true - update only current status, false - update current and previous status
     */
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

    /**
     * Update entity unchanged status
     *
     * @param onlyCurrent Boolean true - update only current status, false - update current and previous status
     */
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

    /**
     * Add attribute value to entity
     *
     * @param attribute AnyTypeAttribute
     * @param value Type
     */
    fun addAttribute(attribute: AnyTypeAttribute, value: Type) =
        addAttribute(this, attribute, value)

    /**
     * Update entity attribute value
     *
     * @param attribute AnyTypeAttribute
     * @param value Type
     */
    fun updateAttribute(attribute: AnyTypeAttribute, value: Type) =
        updateAttribute(this, attribute, value)

    /**
     * Remove attribute value from entity
     *
     * @param attribute AnyTypeAttribute
     */
    fun removeAttribute(attribute: AnyTypeAttribute) =
        removeAttribute(this, attribute)

    /**
     * Read attribute value from DB
     *
     * @param attribute AnyTypeAttribute
     * @return Type?
     */
    fun readAttribute(attribute: AnyTypeAttribute) =
        readAttribute(this, attribute)

    /**
     * Read all attributes values from DB
     *
     * @return Map<Attribute<out Type>, Type?>
     */
    fun readAttributes() =
        readAttributes(this)

    /**
     * Exposed entity class for entity attributes
     */
    companion object : DbEntityClass(
        EntityVarchar,
        EntityLong,
        EntityDouble,
        EntityBoolean,
        EntityDateTime,
        EntityText
    )
}
