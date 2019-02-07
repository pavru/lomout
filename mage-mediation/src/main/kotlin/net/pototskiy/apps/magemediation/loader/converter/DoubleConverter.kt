package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.loader.LoaderException
import java.text.NumberFormat
import java.text.ParseException


class DoubleConverter(
    private val value: Any,
    private val attrDesc: Attribute
) {
    fun convert(): Double = when (value) {
        is Long -> value.toDouble()
        is Double -> value
        is Boolean -> if (value) 1.0 else 0.0
        is String -> stringToDouble(value)
        else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
    }

    fun convertList(): List<Double> {
        return when (value) {
            is Long -> listOf(value.toDouble())
            is Double -> listOf(value)
            is Boolean -> listOf(if (value) 1.0 else 0.0)
            is String -> {
                ValueListParser(
                    value,
                    attrDesc.type
                )
                    .parse()
                    .map { stringToDouble(it) }
            }
            else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
        }
    }

    private fun stringToDouble(value: String): Double {
        val format = NumberFormat.getInstance(attrDesc.type.getLocaleObject())
        try {
            return format.parse(value).toDouble()
        } catch (e: ParseException) {
            throw LoaderException("Field<${attrDesc.name}>, string can not be converted to double")
        }
    }
}
