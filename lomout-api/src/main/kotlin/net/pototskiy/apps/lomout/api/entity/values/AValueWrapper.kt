package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badData
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BOOLEANLIST
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATELIST
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DATETIMELIST
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.DOUBLELIST
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LONGLIST
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.type.TEXTLIST
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.joda.time.DateTime

/**
 * Wrap Any kotlin value to [Type].
 *
 * @param attribute The destination attribute
 * @param value The value to wrap
 * @return The wrapped value
 * @throws AppDataException The value cannot be wrapped to [Type]
 */
@Suppress("ComplexMethod")
fun wrapAValue(attribute: AnyTypeAttribute, value: Any): Type {
    @Suppress("UNCHECKED_CAST")
    return when (attribute.type) {
        BOOLEAN::class -> (value as? Boolean)?.let { BOOLEAN(it) }
        LONG::class -> (value as? Long)?.let { LONG(it) }
        DOUBLE::class -> (value as? Double)?.let { DOUBLE(it) }
        STRING::class -> (value as? String)?.let { STRING(it) }
        DATE::class -> (value as? DateTime)?.let { DATE(it) }
        DATETIME::class -> (value as? DateTime)?.let { DATETIME(it) }
        TEXT::class -> (value as? String)?.let { TEXT(it) }
        BOOLEANLIST::class -> (value as? List<Boolean>)?.let { list ->
            BOOLEANLIST(list.map { BOOLEAN(it) })
        }
        LONGLIST::class -> (value as? List<Long>)?.let { list ->
            LONGLIST(list.map { LONG(it) })
        }
        DOUBLELIST::class -> (value as? List<Double>)?.let { list ->
            DOUBLELIST(list.map { DOUBLE(it) })
        }
        STRINGLIST::class -> (value as? List<String>)?.let { list ->
            STRINGLIST(list.map { STRING(it) })
        }
        DATELIST::class -> (value as? List<DateTime>)?.let { list ->
            DATELIST(list.map { DATE(it) })
        }
        DATETIMELIST::class -> (value as? List<DateTime>)?.let { list ->
            DATETIMELIST(list.map { DATETIME(it) })
        }
        TEXTLIST::class -> (value as? List<String>)?.let { list ->
            TEXTLIST(list.map { TEXT(it) })
        }
        ATTRIBUTELIST::class -> (value as? Map<String, Cell>)?.let { ATTRIBUTELIST(it) }
        else -> throw AppDataException(
            badData(value) + attribute,
            "Unexpected type."
        )
    } ?: throw AppDataException(badData(value) + attribute, "Cannot wrap value.")
}
