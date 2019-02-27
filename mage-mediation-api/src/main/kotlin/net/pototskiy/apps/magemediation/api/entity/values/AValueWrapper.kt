package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.joda.time.DateTime

fun wrapAValue(attribute: AnyTypeAttribute, value: Any?): Type? {
    if (value == null) return null
    @Suppress("UNCHECKED_CAST")
    return when (attribute.valueType) {
        BooleanType::class -> (value as? Boolean)?.let { BooleanValue(it) }
        LongType::class -> (value as? Long)?.let { LongValue(it) }
        DoubleType::class -> (value as? Double)?.let { DoubleValue(it) }
        StringType::class -> (value as? String)?.let { StringValue(it) }
        DateType::class -> (value as? DateTime)?.let { DateValue(it) }
        DateTimeType::class -> (value as? DateTime)?.let { DateTimeValue(it) }
        TextType::class -> (value as? String)?.let { TextValue(it) }
        BooleanListType::class -> (value as? List<Boolean>)?.let { list ->
            BooleanListValue(list.map { BooleanValue(it) })
        }
        LongListType::class -> (value as? List<Long>)?.let { list ->
            LongListValue(list.map { LongValue(it) })
        }
        DoubleListType::class -> (value as? List<Double>)?.let { list ->
            DoubleListValue(list.map { DoubleValue(it) })
        }
        StringListType::class -> (value as? List<String>)?.let { list ->
            StringListValue(list.map { StringValue(it) })
        }
        DateListType::class -> (value as? List<DateTime>)?.let { list ->
            DateListValue(list.map { DateValue(it) })
        }
        DateTimeListType::class -> (value as? List<DateTime>)?.let { list ->
            DateTimeListValue(list.map { DateTimeValue(it) })
        }
        TextListType::class -> (value as? List<String>)?.let { list ->
            TextListValue(list.map { TextValue(it) })
        }
        AttributeListType::class -> (value as? Map<String, Cell>)?.let { AttributeListValue(it) }
        else -> throw SourceException("Unexpected type<${attribute.valueType.simpleName}>")
    } ?: throw DatabaseException("Can not wrap value to ${attribute.valueType.simpleName}")
}
