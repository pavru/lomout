package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.joda.time.DateTime

private const val VARCHAR_LENGTH = 1000

/**
 * String, StringList attribute DB table
 */
object EntityVarchars : AttributeTable<String>(
    "entity_string",
    DbEntityTable,
    VarCharColumnType(VARCHAR_LENGTH)
)

/**
 * String, StringList attribute Exposed entity
 *
 * @constructor
 * @param id EntityID<Int> The entity internal id
 */
class EntityVarchar(id: EntityID<Int>) : AttributeEntity<String>(id) {
    /**
     * Companion object
     */
    companion object : AttributeEntityClass<String, EntityVarchar>(
        EntityVarchars
    )
}

/**
 * Long, LongList attribute DB table
 */
object EntityLongs : AttributeTable<Long>(
    "entity_long",
    DbEntityTable,
    LongColumnType()
)

/**
 * Long, LongList attribute Exposed entity
 *
 * @constructor
 * @param id EntityID<Int> The entity internal id
 */
class EntityLong(id: EntityID<Int>) : AttributeEntity<Long>(id) {
    /**
     * Companion object
     */
    companion object : AttributeEntityClass<Long, EntityLong>(EntityLongs)
}

/**
 * Double, DoubleList attribute DB table
 */
object EntityDoubles : AttributeTable<Double>(
    "entity_double",
    DbEntityTable,
    DoubleColumnType()
)

/**
 * Double, DoubleList attribute Exposed entity
 *
 * @constructor
 * @param id EntityID<Int> The entity internal id
 */
class EntityDouble(id: EntityID<Int>) : AttributeEntity<Double>(id) {
    /**
     * Companion object
     */
    companion object : AttributeEntityClass<Double, EntityDouble>(
        EntityDoubles
    )
}

/**
 * Boolean, BooleanList attribute DB table
 */
object EntityBooleans : AttributeTable<Boolean>(
    "entity_bool",
    DbEntityTable,
    BooleanColumnType()
)

/**
 * Boolean, BooleanList Exposed entity
 *
 * @constructor
 * @param id EntityID<Int> The entity internal id
 */
class EntityBoolean(id: EntityID<Int>) : AttributeEntity<Boolean>(id) {
    /**
     * Companion object
     */
    companion object : AttributeEntityClass<Boolean, EntityBoolean>(
        EntityBooleans
    )
}

/**
 * DateTime, DateTimeList DB table
 */
object EntityDateTimes : AttributeTable<DateTime>(
    "entity_datetime",
    DbEntityTable,
    DateColumnType(true)
)

/**
 * DateTime, DateTimeList Exposed entity
 *
 * @constructor
 * @param id EntityID<Int> The entity internal id
 */
class EntityDateTime(id: EntityID<Int>) : AttributeEntity<DateTime>(id) {
    /**
     * Companion object
     */
    companion object : AttributeEntityClass<DateTime, EntityDateTime>(
        EntityDateTimes
    )
}

/**
 * Text attribute DB table
 */
object EntityTexts : AttributeTable<String>(
    "entity_text",
    DbEntityTable,
    TextColumnType()
)

/**
 * Text attribute Exposed entity
 *
 * @constructor
 * @param id EntityID<Int> The entity internal id
 */
class EntityText(id: EntityID<Int>) : AttributeEntity<String>(id) {
    /**
     * Companion object
     */
    companion object : AttributeEntityClass<String, EntityText>(EntityTexts)
}
