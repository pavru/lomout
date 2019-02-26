package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.source.workbook.*
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
            return when (attribute.valueType) {
                BooleanType::class -> CellType.BOOL
                LongType::class -> CellType.LONG
                DoubleType::class -> CellType.DOUBLE
                StringType::class -> CellType.STRING
                DateType::class -> CellType.DOUBLE
                DateTimeType::class -> CellType.DOUBLE
                TextType::class -> CellType.STRING
                BooleanListType::class,
                LongListType::class,
                DoubleListType::class,
                StringListType::class,
                DateListType::class,
                DateTimeListType::class -> CellType.STRING
                AttributeListType::class -> CellType.BLANK
                else -> CellType.BLANK
            }
        }

    override val booleanValue: Boolean
        get() = when (attribute.valueType) {
            BooleanType::class -> (aValue as BooleanValue).value
            else -> throw SourceException(DATA_INCOMPATIBLE_MSG)
        }
    override val longValue: Long
        get() = when (attribute.valueType) {
            LongType::class -> (aValue as LongValue).value
            else -> throw SourceException(DATA_INCOMPATIBLE_MSG)
        }
    override val doubleValue: Double
        get() = when (attribute.valueType) {
            DoubleType::class -> (aValue as DoubleValue).value
            DateType::class -> HSSFDateUtil.getExcelDate((aValue as DateTimeValue).value.toDate())
            DateTimeType::class -> HSSFDateUtil.getExcelDate((aValue as DateTimeValue).value.toDate())
            else -> throw SourceException(DATA_INCOMPATIBLE_MSG)
        }
    override val stringValue: String
        get() = when (attribute.valueType) {
            BooleanType::class -> if ((aValue as BooleanValue).value) "1" else "0"
            LongType::class -> (aValue as LongValue).value.toString()
            DoubleType::class -> (aValue as DoubleValue).value.toString()
            StringType::class -> (aValue as StringValue).value
            DateType::class -> (aValue as DateValue).value.toString()
            DateTimeType::class -> (aValue as DateTimeValue).value.toString()
            TextType::class -> (aValue as TextValue).value
            BooleanListType::class ->
                (aValue as BooleanListValue).value.joinToString(",") { it.toString() }
            LongListType::class ->
                (aValue as LongListValue).value.joinToString(",") { it.toString() }
            DoubleListType::class ->
                (aValue as DoubleListValue).value.joinToString(",") { it.toString() }
            StringListType::class ->
                (value as StringListValue).value.joinToString(",")
            DateListType::class -> (aValue as DateListValue).value.joinToString(",") { it.toString() }
            DateTimeListType::class ->
                (aValue as DateTimeListValue).value.joinToString(",") { it.toString() }
            AttributeListType::class -> ""
            else -> ""
        }
    override val row: Row
        get() = throw NotImplementedError("Attribute cell has no row")

    override fun asString(): String {
        return stringValue
    }

    override fun setCellValue(value: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCellValue(value: DateTime) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val DATA_INCOMPATIBLE_MSG = "Data type is incompatible"
    }
}
