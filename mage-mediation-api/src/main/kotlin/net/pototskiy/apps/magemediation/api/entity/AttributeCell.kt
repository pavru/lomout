package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.NOT_IMPLEMENTED
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime

class AttributeCell<T : Type>(
    private val attribute: Attribute<out T>,
    private val aValue: T?
) : Cell {
    override val address = CellAddress(0, 0)
    override val cellType: CellType
        get() {
            if (aValue == null) return CellType.BLANK
            return when {
                attribute.valueType.isTypeOf<BooleanType>() -> CellType.BOOL
                attribute.valueType.isTypeOf<LongType>() -> CellType.LONG
                attribute.valueType.isTypeOf<DoubleType>() -> CellType.DOUBLE
                attribute.valueType.isTypeOf<StringType>() -> CellType.STRING
                attribute.valueType.isTypeOf<DateType>() -> CellType.DOUBLE
                attribute.valueType.isTypeOf<DateTimeType>() -> CellType.DOUBLE
                attribute.valueType.isTypeOf<TextType>() -> CellType.STRING
                attribute.valueType.isTypeOf<BooleanListType>() ||
                        attribute.valueType.isTypeOf<LongListType>() ||
                        attribute.valueType.isTypeOf<DoubleListType>() ||
                        attribute.valueType.isTypeOf<StringListType>() ||
                        attribute.valueType.isTypeOf<DateListType>()
                        || attribute.valueType.isTypeOf<DateTimeListType>() -> CellType.STRING
                attribute.valueType.isTypeOf<AttributeListType>() -> CellType.BLANK
                else -> CellType.BLANK
            }
        }

    override val booleanValue: Boolean
        get() = when {
            attribute.valueType.isTypeOf<BooleanType>() -> (aValue as BooleanValue).value
            else -> throw SourceException(DATA_INCOMPATIBLE_MSG)
        }
    override val longValue: Long
        get() = when {
            attribute.valueType.isTypeOf<LongType>() -> (aValue as LongValue).value
            else -> throw SourceException(DATA_INCOMPATIBLE_MSG)
        }
    override val doubleValue: Double
        get() = when {
            attribute.valueType.isTypeOf<DoubleType>() -> (aValue as DoubleValue).value
            attribute.valueType.isTypeOf<DateType>() ->
                HSSFDateUtil.getExcelDate((aValue as DateTimeValue).value.toDate())
            attribute.valueType.isTypeOf<DateTimeType>() ->
                HSSFDateUtil.getExcelDate((aValue as DateTimeValue).value.toDate())
            else -> throw SourceException(DATA_INCOMPATIBLE_MSG)
        }
    override val stringValue: String
        get() = when {
            attribute.valueType.isTypeOf<BooleanType>() -> if ((aValue as BooleanValue).value) "1" else "0"
            attribute.valueType.isTypeOf<LongType>() -> (aValue as LongValue).value.toString()
            attribute.valueType.isTypeOf<DoubleType>() -> (aValue as DoubleValue).value.toString()
            attribute.valueType.isTypeOf<StringType>() -> (aValue as StringValue).value
            attribute.valueType.isTypeOf<DateType>() -> (aValue as DateValue).value.toString()
            attribute.valueType.isTypeOf<DateTimeType>() -> (aValue as DateTimeValue).value.toString()
            attribute.valueType.isTypeOf<TextType>() -> (aValue as TextValue).value
            attribute.valueType.isTypeOf<BooleanListType>() ->
                (aValue as BooleanListValue).value.joinToString(",") { it.toString() }
            attribute.valueType.isTypeOf<LongListType>() ->
                (aValue as LongListValue).value.joinToString(",") { it.toString() }
            attribute.valueType.isTypeOf<DoubleListType>() ->
                (aValue as DoubleListValue).value.joinToString(",") { it.toString() }
            attribute.valueType.isTypeOf<StringListType>() ->
                (value as StringListValue).value.joinToString(",")
            attribute.valueType.isTypeOf<DateListType>() ->
                (aValue as DateListValue).value.joinToString(",") { it.toString() }
            attribute.valueType.isTypeOf<DateTimeListType>() ->
                (aValue as DateTimeListValue).value.joinToString(",") { it.toString() }
            attribute.valueType.isTypeOf<AttributeListType>() -> ""
            else -> ""
        }
    override val row: Row
        get() = throw NotImplementedError("Attribute cell has no row")

    override fun asString(): String {
        return stringValue
    }

    override fun setCellValue(value: String) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Boolean) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Long) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Double) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: DateTime) {
        TODO(NOT_IMPLEMENTED) // To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val DATA_INCOMPATIBLE_MSG = "Data type is incompatible"
    }
}
