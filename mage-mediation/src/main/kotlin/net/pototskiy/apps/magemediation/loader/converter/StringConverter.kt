package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.loader.LoaderException
import java.text.NumberFormat

class StringConverter(
    private val value: Any,
    private val attrDesc: Attribute
) {
    fun convert(): String {
        return when (value) {
            is Long -> intToString(value)
            is Double -> doubleToString(value)
            is Boolean -> value.toString()
            is String -> value
            else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
        }
    }

    fun convertList(): List<String> {
        return when (value) {
            is Long -> listOf(intToString(value))
            is Double -> listOf(doubleToString(value))
            is Boolean -> listOf(value.toString())
            is String -> ValueListParser(
                value,
                attrDesc.type
            ).parse()
            else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
        }
    }

    private fun doubleToString(value: Double): String {
        val format = NumberFormat.getInstance(attrDesc.type.getLocaleObject())
        format.isGroupingUsed = false
        return format.format(value)
    }

    private fun intToString(value: Long): String {
        val format = NumberFormat.getIntegerInstance(attrDesc.type.getLocaleObject())
        format.isGroupingUsed = false
        return format.format(value)
    }
}
