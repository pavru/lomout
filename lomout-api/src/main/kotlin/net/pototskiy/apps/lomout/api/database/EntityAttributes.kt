@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.joda.time.DateTime

private const val VARCHAR_LENGTH = 950

/**
 * String, StringList attribute DB table
 */
object EntityVarchars : AttributeTable<String>(
    "entity_string",
    DbEntityTable,
    VarCharColumnType(VARCHAR_LENGTH)
)

/**
 * Alias for EntityVarchars
 */
val StringAttrTab = EntityVarchars
/**
 * Alias for EntityVarchars.id
 */
val StringAttrId = EntityVarchars.id
/**
 * Alias for EntityVarchars.owner
 */
val StringAttrOwner = EntityVarchars.owner
/**
 * Alias for EntityVarchars.code
 */
val StringAttrCode = EntityVarchars.code
/**
 * Alias for EntityVarchars.value
 */
val StringAttrValue = EntityVarchars.value

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
 * Alias for EntityLongs
 */
val LongAttrTab = EntityLongs
/**
 * Alias for EntityLongs.id
 */
val LongAttrId = EntityLongs.id
/**
 * Alias for EntityLongs.owner
 */
val LongAttrOwner = EntityLongs.owner
/**
 * Alias for EntityLongs.code
 */
val LongAttrCode = EntityLongs.code
/**
 * Alias for EntityLongs.value
 */
val LongAttrValue = EntityLongs.value

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
 * Alias for EntityDoubles
 */
val DoubleAttrTab = EntityDoubles
/**
 * Alias for EntityDoubles.id
 */
val DoubleAttrId = EntityDoubles.id
/**
 * Alias for EntityDoubles.owner
 */
val DoubleAttrOwner = EntityDoubles.owner
/**
 * Alias for EntityDoubles.code
 */
val DoubleAttrCode = EntityDoubles.code
/**
 * Alias for EntityDoubles.value
 */
val DoubleAttrValue = EntityDoubles.value

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
 * Alias for EntityBooleans
 */
val BooleanAttrTab = EntityBooleans
/**
 * Alias for EntityBooleans.id
 */
val BooleanAttrId = EntityBooleans.id
/**
 * Alias for EntityBooleans.owner
 */
val BooleanAttrOwner = EntityBooleans.owner
/**
 * Alias for EntityBooleans.code
 */
val BooleanAttrCode = EntityBooleans.code
/**
 * Alias for EntityBooleans.value
 */
val BooleanAttrValue = EntityBooleans.value

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
 * Alias for EntityDateTimes
 */
val DateTimeAttrTab = EntityDateTimes
/**
 * Alias for EntityDateTimes.id
 */
val DateTimeAttrId = EntityDateTimes.id
/**
 * Alias for EntityDateTimes.owner
 */
val DateTimeAttrOwner = EntityDateTimes.owner
/**
 * Alias for EntityDateTimes.code
 */
val DateTimeAttrCode = EntityDateTimes.code
/**
 * Alias for EntityDateTimes.value
 */
val DateTimeAttrValue = EntityDateTimes.value

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
 * Alias for EntityTexts
 */
val TextAttrTab = EntityTexts
/**
 * Alias for EntityTexts.id
 */
val TextAttrId = EntityTexts.id
/**
 * Alias for EntityTexts.owner
 */
val TextAttrOwner = EntityTexts.owner
/**
 * Alias for EntityTexts.code
 */
val TextAttrCode = EntityTexts.code
/**
 * Alias for EntityTexts.value
 */
val TextAttrValue = EntityTexts.value

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
