package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.joda.time.DateTime

class BooleanValue(value: Boolean, isTransient: Boolean = false) : BooleanType(value, isTransient)
class LongValue(value: Long, isTransient: Boolean = false) : LongType(value, isTransient)
class DoubleValue(value: Double, isTransient: Boolean = false) : DoubleType(value, isTransient)
class StringValue(value: String, isTransient: Boolean = false) : StringType(value, isTransient)
class DateValue(value: DateTime, isTransient: Boolean = false) : DateType(value, isTransient)
class DateTimeValue(value: DateTime, isTransient: Boolean = false) : DateTimeType(value, isTransient)
class TextValue(value: String, isTransient: Boolean = false) : TextType(value, isTransient)
class BooleanListValue(value: List<BooleanType>, isTransient: Boolean = false) : BooleanListType(value, isTransient)
class LongListValue(value: List<LongType>, isTransient: Boolean = false) : LongListType(value, isTransient)
class DoubleListValue(value: List<DoubleType>, isTransient: Boolean = false) : DoubleListType(value, isTransient)
class StringListValue(value: List<StringType>, isTransient: Boolean = false) : StringListType(value, isTransient)
class TextListValue(value: List<TextType>, isTransient: Boolean = false) : TextListType(value, isTransient)
class DateListValue(value: List<DateType>, isTransient: Boolean = false) : DateListType(value, isTransient)
class DateTimeListValue(value: List<DateTimeType>, isTransient: Boolean = false) : DateTimeListType(value, isTransient)
class AttributeListValue(value: Map<String, Cell>, isTransient: Boolean = true) : AttributeListType(value, isTransient)

fun Type.toList(): ListType<*> {
    return when (this) {
        is BooleanListType,
        is LongListType,
        is DoubleListType,
        is StringListType,
        is TextListType,
        is DateListType,
        is DateTimeListType,
        is AttributeListType -> throw SourceException("Value already is list")
        is BooleanType -> BooleanListValue(listOf(this))
        is LongType -> LongListValue(listOf(this))
        is DoubleType -> DoubleListValue(listOf(this))
        is StringType -> StringListValue(listOf(this))
        is DateType -> DateListValue(listOf(this))
        is DateTimeType -> DateTimeListValue(listOf(this))
        is TextType -> TextListValue(listOf(this))
    }
}
