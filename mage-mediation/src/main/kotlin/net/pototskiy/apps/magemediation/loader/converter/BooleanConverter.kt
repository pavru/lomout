package net.pototskiy.apps.magemediation.loader.converter

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.loader.LoaderException

class BooleanConverter(
    private val value: Any,
    private val attrDesc: Attribute
) {
    fun convert(): Boolean {
        return when (value) {
            is Boolean -> value
            is String -> stringToBoolean(value)
            is Long -> value != 0L
            is Double -> value != 0.0
            else -> throw LoaderException("Conversion for type<${value::class.simpleName}> is not supported")
        }
    }

    private fun stringToBoolean(value: String): Boolean {
        val v = value.toLowerCase().trim()
        return if (v in stringBoolean)
            v in stringBooleanTrue
        else
            throw LoaderException("Field<${attrDesc.name}>, string can not converted to boolean")
    }

    fun convertList(): List<Boolean> {
        return when (value) {
            is Long -> listOf(value != 0L)
            is Double -> listOf(value != 0.0)
            is Boolean -> listOf(value)
            is String -> {
                ValueListParser(
                    value,
                    attrDesc.type
                )
                    .parse()
                    .map { stringToBoolean(it) }
            }
            else -> throw LoaderException("Field<${attrDesc.name}>, string can not converted to boolean")
        }
    }

    companion object {
        val stringBoolean = listOf("1", "yes", "true", "0", "no", "false")
        val stringBooleanTrue = listOf("1", "yes", "true")
    }
}
