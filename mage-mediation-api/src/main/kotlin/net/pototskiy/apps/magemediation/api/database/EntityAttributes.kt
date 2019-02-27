package net.pototskiy.apps.magemediation.api.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

object EntityVarchars : AttributeTable<String>(
    "entity_string",
    DbEntityTable,
    VarCharColumnType(1000)
)

class EntityVarchar(id: EntityID<Int>) : AttributeEntity<String>(id) {
    companion object : AttributeEntityClass<String, EntityVarchar>(
        EntityVarchars
    )
}

object EntityLongs : AttributeTable<Long>(
    "entity_long",
    DbEntityTable,
    LongColumnType()
)

class EntityLong(id: EntityID<Int>) : AttributeEntity<Long>(id) {
    companion object : AttributeEntityClass<Long, EntityLong>(EntityLongs)
}

object EntityDoubles : AttributeTable<Double>(
    "entity_double",
    DbEntityTable,
    DoubleColumnType()
)

class EntityDouble(id: EntityID<Int>) : AttributeEntity<Double>(id) {
    companion object : AttributeEntityClass<Double, EntityDouble>(
        EntityDoubles
    )
}

object EntityBooleans : AttributeTable<Boolean>(
    "entity_bool",
    DbEntityTable,
    BooleanColumnType()
)

class EntityBoolean(id: EntityID<Int>) : AttributeEntity<Boolean>(id) {
    companion object : AttributeEntityClass<Boolean, EntityBoolean>(
        EntityBooleans
    )
}

object EntityDateTimes : AttributeTable<DateTime>(
    "entity_datetime",
    DbEntityTable,
    DateColumnType(true)
)

class EntityDateTime(id: EntityID<Int>) : AttributeEntity<DateTime>(id) {
    companion object : AttributeEntityClass<DateTime, EntityDateTime>(
        EntityDateTimes
    )
}

object EntityTexts : AttributeTable<String>(
    "entity_text",
    DbEntityTable,
    TextColumnType()
)

class EntityText(id: EntityID<Int>) : AttributeEntity<String>(id) {
    companion object : AttributeEntityClass<String, EntityText>(EntityTexts)
}
