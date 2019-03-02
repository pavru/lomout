package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.AttributeListValue
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanListValue
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.BooleanValue
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateListValue
import net.pototskiy.apps.magemediation.api.entity.DateTimeListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeListValue
import net.pototskiy.apps.magemediation.api.entity.DateTimeType
import net.pototskiy.apps.magemediation.api.entity.DateTimeValue
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.DateValue
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleListValue
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.DoubleValue
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongListValue
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.LongValue
import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringListValue
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.StringValue
import net.pototskiy.apps.magemediation.api.entity.TextListType
import net.pototskiy.apps.magemediation.api.entity.TextListValue
import net.pototskiy.apps.magemediation.api.entity.TextType
import net.pototskiy.apps.magemediation.api.entity.TextValue
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.joda.time.DateTime

@Suppress("ComplexMethod")
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
