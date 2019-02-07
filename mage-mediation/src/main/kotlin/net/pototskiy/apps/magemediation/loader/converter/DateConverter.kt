package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.loader.LoaderException
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class DateConverter(
    private val value: Any,
    private val attrDesc: Attribute
) {
    fun convert(): DateTime = when (value) {
        is Long -> DateTime(Date(value))
        is Double -> DateTime(HSSFDateUtil.getJavaDate(value))
        is Boolean -> throw LoaderException("Field<${attrDesc.name}, boolean can not be converted to date")
        is String -> stringToDate(value)
        else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
    }

    fun convertList(): List<DateTime> {
        return when (value) {
            is Long -> listOf(DateTime(Date(value)))
            is Double -> listOf(DateTime(HSSFDateUtil.getJavaDate(value)))
            is Boolean -> throw LoaderException("Field<${attrDesc.name}, boolean can not be converted to date")
            is String -> ValueListParser(value, attrDesc.type)
                .parse()
                .map { stringToDate(it) }
            else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
        }
    }

    private fun stringToDate(value: String): DateTime {
        val errorMsg: String
        val format = if (attrDesc.type.hasPattern) {
            errorMsg = "pattern ${attrDesc.type.pattern}"
            DateTimeFormat.forPattern(attrDesc.type.pattern)
        } else {
            errorMsg = "locale ${attrDesc.type.locale}"
            DateTimeFormat.shortDate().withLocale(attrDesc.type.getLocaleObject())
        }
        return try {
            format.parseDateTime(value)
        } catch (e: IllegalArgumentException) {
            throw LoaderException("Field<${attrDesc.name}>, string can not be converted to date with $errorMsg.")
        } catch (e: UnsupportedOperationException) {
            throw LoaderException("Field<${attrDesc.name}>, string can not be converted to date with $errorMsg")
        }
    }
}
