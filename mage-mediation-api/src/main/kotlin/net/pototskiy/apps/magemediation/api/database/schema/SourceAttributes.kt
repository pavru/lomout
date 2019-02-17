package net.pototskiy.apps.magemediation.api.database.schema

import net.pototskiy.apps.magemediation.api.database.AttributeEntity
import net.pototskiy.apps.magemediation.api.database.AttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.AttributeTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

object SourceVarchars : AttributeTable<String>(
    "source_varchar",
    SourceEntities,
    VarCharColumnType(1000)
)

class SourceVarchar(id: EntityID<Int>) : AttributeEntity<String>(id) {
    companion object : AttributeEntityClass<String, SourceVarchar>(
        SourceVarchars
    )
}

object SourceLongs : AttributeTable<Long>(
    "source_long",
    SourceEntities,
    LongColumnType()
)

class SourceLong(id: EntityID<Int>) : AttributeEntity<Long>(id) {
    companion object : AttributeEntityClass<Long, SourceLong>(SourceLongs)
}

object SourceDoubles : AttributeTable<Double>(
    "source_double",
    SourceEntities,
    DoubleColumnType()
)

class SourceDouble(id: EntityID<Int>) : AttributeEntity<Double>(id) {
    companion object : AttributeEntityClass<Double, SourceDouble>(
        SourceDoubles
    )
}

object SourceBooleans : AttributeTable<Boolean>(
    "source_bool",
    SourceEntities,
    BooleanColumnType()
)

class SourceBoolean(id: EntityID<Int>) : AttributeEntity<Boolean>(id) {
    companion object : AttributeEntityClass<Boolean, SourceBoolean>(
        SourceBooleans
    )
}

object SourceDates : AttributeTable<DateTime>(
    "source_date",
    SourceEntities,
    DateColumnType(false)
)

class SourceDate(id: EntityID<Int>) : AttributeEntity<DateTime>(id) {
    companion object : AttributeEntityClass<DateTime, SourceDate>(
        SourceDates
    )
}

object SourceDateTimes : AttributeTable<DateTime>(
    "source_datetime",
    SourceEntities,
    DateColumnType(true)
)

class SourceDateTime(id: EntityID<Int>) : AttributeEntity<DateTime>(id) {
    companion object : AttributeEntityClass<DateTime, SourceDateTime>(
        SourceDateTimes
    )
}

object SourceTexts : AttributeTable<String>(
    "source_text",
    SourceEntities,
    TextColumnType()
)

class SourceText(id: EntityID<Int>) : AttributeEntity<String>(id) {
    companion object : AttributeEntityClass<String, SourceText>(SourceTexts)
}
