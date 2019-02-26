package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.entity.values.stringToBoolean
import net.pototskiy.apps.magemediation.api.entity.values.stringToDouble
import net.pototskiy.apps.magemediation.api.entity.values.stringToLong
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellAddress
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import org.joda.time.DateTime

class CsvCell(
    private val _address: CellAddress,
    private val _value: String,
    private val _row: CsvRow
) : Cell {
    override val address: CellAddress
        get() = _address
    override val cellType: CellType
        get() = _value.recognize()
    override val booleanValue: Boolean
        get() = _value.trim().stringToBoolean(workbookLocale)
    override val longValue: Long
        get() = _value.stringToLong(workbookLocale)
    override val doubleValue: Double
        get() = _value.stringToDouble(workbookLocale)
    override val stringValue: String
        get() = _value

    private val workbookLocale = (_row.sheet.workbook as CsvWorkbook).workbookLocale

    override fun asString(): String {
        return _value
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

    override val row: Row
        get() = _row

    private fun String.recognize(): CellType {
        return when {
            try {
                this.stringToLong(workbookLocale)
                true
            } catch (e: Exception) {
                false
            } -> CellType.LONG
            try {
                this.stringToDouble(workbookLocale)
                true
            } catch (e:Exception) {
                false
            } -> CellType.DOUBLE
            try {
                this.toLowerCase().trim().stringToBoolean(workbookLocale)
                true
            } catch (e: Exception) {
                false
            } -> CellType.BOOL
            this.isNotBlank() -> CellType.STRING
            else -> CellType.BLANK
        }
    }
}
