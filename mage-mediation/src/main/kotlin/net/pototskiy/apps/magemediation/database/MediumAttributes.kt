package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.database.newschema.AttributeEntity
import net.pototskiy.apps.magemediation.api.database.newschema.AttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.newschema.AttributeTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

object MediumVarchars : AttributeTable<String>(
    "medium_varchar",
    MediumEntities,
    VarCharColumnType(1000)
)

class MediumVarchar(id: EntityID<Int>) : AttributeEntity<String>(id) {
    companion object : AttributeEntityClass<String, MediumVarchar>(MediumVarchars)
}

object MediumLongs : AttributeTable<Long>(
    "medium_varchar",
    MediumEntities,
    LongColumnType()
)

class MediumLong(id: EntityID<Int>) : AttributeEntity<Long>(id) {
    companion object : AttributeEntityClass<Long, MediumLong>(MediumLongs)
}

object MediumDoubles : AttributeTable<Double>(
    "medium_double",
    MediumEntities,
    DoubleColumnType()
)

class MediumDouble(id: EntityID<Int>) : AttributeEntity<Double>(id) {
    companion object : AttributeEntityClass<Double, MediumDouble>(MediumDoubles)
}

object MediumBooleans : AttributeTable<Boolean>(
    "medium_bool",
    MediumEntities,
    BooleanColumnType()
)

class MediumBoolean(id: EntityID<Int>) : AttributeEntity<Boolean>(id) {
    companion object : AttributeEntityClass<Boolean, MediumBoolean>(MediumBooleans)
}

object MediumDates : AttributeTable<DateTime>(
    "medium_date",
    MediumEntities,
    DateColumnType(false)
)

class MediumDate(id: EntityID<Int>) : AttributeEntity<DateTime>(id) {
    companion object : AttributeEntityClass<DateTime, MediumDate>(MediumDates)
}

object MediumDateTimes : AttributeTable<DateTime>(
    "medium_datetime",
    MediumEntities,
    DateColumnType(true)
)

class MediumDateTime(id: EntityID<Int>) : AttributeEntity<DateTime>(id) {
    companion object : AttributeEntityClass<DateTime, MediumDateTime>(MediumDateTimes)
}

object MediumTexts : AttributeTable<String>(
    "medium_text",
    MediumEntities,
    TextColumnType()
)

class MediumText(id: EntityID<Int>) : AttributeEntity<String>(id) {
    companion object : AttributeEntityClass<String, MediumText>(MediumTexts)
}
