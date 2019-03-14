package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeType
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.TextListType
import net.pototskiy.apps.magemediation.api.entity.TextType
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import org.joda.time.DateTime

@Suppress("ComplexMethod")
fun wrapAValue(attribute: AnyTypeAttribute, value: Any?): Type? {
    if (value == null) return null
    @Suppress("UNCHECKED_CAST")
    return when (attribute.valueType) {
        BooleanType::class -> (value as? Boolean)?.let { BooleanType(it) }
        LongType::class -> (value as? Long)?.let { LongType(it) }
        DoubleType::class -> (value as? Double)?.let { DoubleType(it) }
        StringType::class -> (value as? String)?.let { StringType(it) }
        DateType::class -> (value as? DateTime)?.let { DateType(it) }
        DateTimeType::class -> (value as? DateTime)?.let { DateTimeType(it) }
        TextType::class -> (value as? String)?.let { TextType(it) }
        BooleanListType::class -> (value as? List<Boolean>)?.let { list ->
            BooleanListType(list.map { BooleanType(it) })
        }
        LongListType::class -> (value as? List<Long>)?.let { list ->
            LongListType(list.map { LongType(it) })
        }
        DoubleListType::class -> (value as? List<Double>)?.let { list ->
            DoubleListType(list.map { DoubleType(it) })
        }
        StringListType::class -> (value as? List<String>)?.let { list ->
            StringListType(list.map { StringType(it) })
        }
        DateListType::class -> (value as? List<DateTime>)?.let { list ->
            DateListType(list.map { DateType(it) })
        }
        DateTimeListType::class -> (value as? List<DateTime>)?.let { list ->
            DateTimeListType(list.map { DateTimeType(it) })
        }
        TextListType::class -> (value as? List<String>)?.let { list ->
            TextListType(list.map { TextType(it) })
        }
        AttributeListType::class -> (value as? Map<String, Cell>)?.let { AttributeListType(it) }
        else -> throw DatabaseException("Unexpected type<${attribute.valueType.simpleName}>")
    } ?: throw DatabaseException("Can not wrap value to ${attribute.valueType.simpleName}")
}
