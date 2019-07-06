package net.pototskiy.apps.lomout.api.entity.values

import java.text.ParseException
import java.util.*

/**
 * Convert string to boolean according to locale
 *
 * @receiver The boolean value as string
 * @param locale The value locale, supported: en_US, ru_RU
 * @return Value
 * @throws ParseException String cannot be parsed to boolean
 */
fun String.stringToBoolean(locale: Locale): Boolean {
    val v = this.toLowerCase().trim()
    val booleanString = stringBooleanMap[locale]?.booleanString
        ?: defaultMap.booleanString
    val booleanTrueString = stringBooleanMap[locale]?.booleanTrueString
        ?: defaultMap.booleanTrueString
    return if (v in booleanString)
        v in booleanTrueString
    else
        throw ParseException("Value '$this' cannot be converted to boolean.", 0)
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
    ?: stringBooleanMap.getValue(Locale("en", "US"))

private class BooleanStrings(val booleanString: List<String>, val booleanTrueString: List<String>)
