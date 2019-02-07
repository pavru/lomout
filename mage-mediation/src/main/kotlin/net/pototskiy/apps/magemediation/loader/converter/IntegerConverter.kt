package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.floorToLong
import net.pototskiy.apps.magemediation.fraction
import net.pototskiy.apps.magemediation.loader.LoaderException
import java.text.NumberFormat
import java.text.ParseException

class IntegerConverter(
    private val value: Any,
    private val attrDesc: Attribute
) {
    fun convert(): Long = when (value) {
        is Long -> value
        is Double -> doubleToInt(value)
        is Boolean -> if (value) 1 else 0
        is String ->
            stringToInt(value)
                ?: throw LoaderException("Field<${attrDesc.name}>, string can not converted to int")
        else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
    }

    private fun doubleToInt(value: Double): Long {
        return if (value.fraction == 0.0)
            value.floorToLong()
        else
            throw LoaderException("Field<${attrDesc.name}>, double can not converted to int")
    }

    fun convertList(): List<Long> = when (value) {
        is Long -> listOf(value)
        is Double -> listOf(doubleToInt(value))
        is Boolean -> listOf(if (value) 1L else 0L)
        is String -> ValueListParser(
            value,
            attrDesc.type
        )
            .parse()
            .map {
                stringToInt(it)
                    ?: throw LoaderException("Field<${attrDesc.name}>, string can not converted to int")
            }
        else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
    }

    private fun stringToInt(value: String): Long? {
        val format = NumberFormat.getIntegerInstance(attrDesc.type.getLocaleObject())
        return try {
            format.parse(value)?.toLong()
        } catch (e: ParseException) {
            null
        }
    }
}
