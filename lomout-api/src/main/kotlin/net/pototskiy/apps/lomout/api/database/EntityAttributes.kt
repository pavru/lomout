@file:Suppress("unused")

package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BooleanTypeColumnType
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.DateTimeTypeColumnType
import net.pototskiy.apps.lomout.api.entity.type.DateTypeColumnType
import net.pototskiy.apps.lomout.api.entity.type.DoubleTypeColumnType
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LongTypeColumnType
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.StringTypeColumnType
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.type.TextTypeColumnType

private const val VARCHAR_LENGTH = 950

/**
 * String, StringList attribute DB table
 */
internal object EntityStrings : AttributeTable<STRING>(
    "entity_string",
    DbEntityTable,
    StringTypeColumnType(VARCHAR_LENGTH)
)

/**
 * Long, LongList attribute DB table
 */
internal object EntityLongs : AttributeTable<LONG>(
    "entity_long",
    DbEntityTable,
    LongTypeColumnType()
)

/**
 * Double, DoubleList attribute DB table
 */
internal object EntityDoubles : AttributeTable<DOUBLE>(
    "entity_double",
    DbEntityTable,
    DoubleTypeColumnType()
)

/**
 * Boolean, BooleanList attribute DB table
 */
internal object EntityBooleans : AttributeTable<BOOLEAN>(
    "entity_bool",
    DbEntityTable,
    BooleanTypeColumnType()
)

/**
 * DateTime, DateTimeList DB table
 */
internal object EntityDateTimes : AttributeTable<DATETIME>(
    "entity_datetime",
    DbEntityTable,
    DateTimeTypeColumnType()
)

/**
 * DateTime, DateTimeList DB table
 */
internal object EntityDates : AttributeTable<DATE>(
    "entity_date",
    DbEntityTable,
    DateTypeColumnType()
)

/**
 * Text attribute DB table
 */
internal object EntityTexts : AttributeTable<TEXT>(
    "entity_text",
    DbEntityTable,
    TextTypeColumnType()
)
