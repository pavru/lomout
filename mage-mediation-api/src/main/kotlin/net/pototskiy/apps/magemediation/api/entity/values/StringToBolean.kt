package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.plugable.PluginException
import java.util.*


fun String.stringToBoolean(locale: Locale): Boolean {
    val v = this.toLowerCase().trim()
    val booleanString = stringBooleanMap[locale]?.booleanString
        ?: defaultMap.booleanString
    val booleanTrueString = stringBooleanMap[locale]?.booleanTrueString
        ?: defaultMap.booleanTrueString
    return if (v in booleanString)
        v in booleanTrueString
    else
        throw PluginException("Value<$this> can not converted to boolean")
}

private val stringBooleanMap = mapOf(
    Locale("en", "US") to BooleanStrings(
        booleanString = listOf("1", "yes", "true", "0", "no", "false"),
        booleanTrueString = listOf("1", "yes", "true")
    ),
    Locale("ru", "RU") to BooleanStrings(
        booleanString = listOf("1", "да", "истина", "0", "нет", "ложь"),
        booleanTrueString = listOf("1", "да", "истина")
    )
)

private val defaultMap = stringBooleanMap[Locale.getDefault()]
    ?: stringBooleanMap[Locale("en", "US")]!!

private class BooleanStrings(val booleanString: List<String>, val booleanTrueString: List<String>)
