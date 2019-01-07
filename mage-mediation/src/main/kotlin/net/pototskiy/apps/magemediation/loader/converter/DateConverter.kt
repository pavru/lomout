package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Cell
import net.pototskiy.apps.magemediation.source.CellType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class DateConverter(
    private val cell: Cell,
    private val field: FieldConfiguration
) {
    fun convert(): DateTime = when (cell.cellType) {
        CellType.INT -> DateTime(Date(cell.intValue))
        CellType.DOUBLE -> DateTime(HSSFDateUtil.getJavaDate(cell.doubleValue))
        CellType.BOOL -> throw LoaderException("Field<${field.name}, boolean can not be converted to date")
        CellType.STRING -> stringToDate(cell.stringValue)
    }

    fun convertList(): List<DateTime> {
        return when (cell.cellType) {
            CellType.INT -> listOf(DateTime(Date(cell.intValue)))
            CellType.DOUBLE -> listOf(DateTime(HSSFDateUtil.getJavaDate(cell.doubleValue)))
            CellType.BOOL -> throw LoaderException("Field<${field.name}, boolean can not be converted to date")
            CellType.STRING -> ValueListParser(cell.stringValue, field.type)
                .parse()
                .map { stringToDate(it) }
        }
    }

    private fun stringToDate(value: String): DateTime {
        val errorMsg: String
        val format = if (field.type.hasPattern) {
            errorMsg = "pattern ${field.type.pattern}"
            DateTimeFormat.forPattern(field.type.pattern)
        } else {
            errorMsg = "locale ${field.type.locale}"
            DateTimeFormat.shortDate().withLocale(field.type.getLocaleObject())
        }
        return try {
            format.parseDateTime(value)
        } catch (e: IllegalArgumentException) {
            throw LoaderException("Field<${field.name}>, string can not be converted to date with $errorMsg.")
        } catch (e: UnsupportedOperationException) {
            throw LoaderException("Field<${field.name}>, string can not be converted to date with $errorMsg")
        }
    }
}