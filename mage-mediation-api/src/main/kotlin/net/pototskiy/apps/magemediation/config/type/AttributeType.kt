package net.pototskiy.apps.magemediation.config.type

import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
sealed class AttributeType(
    @Suppress("unused") val isList: Boolean,
    val hasLocale: Boolean,
    val hasPattern: Boolean,
    @Suppress("unused") val isAttrList: Boolean
) {
    open val locale: String = ""
    open val pattern: String = ""
    open val quote: String = ""
    open val delimiter: String = ""
    open val valueQuote: String = ""
    open val valueDelimiter: String = ""

    fun getLocaleObject(): Locale = if (hasLocale) {
        val (l, c) = locale.split("_")
        Locale(l, c)
    } else {
        Locale.getDefault()
    }
}

class AttributeStringType(
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(false, hasLocale, false, false)

class AttributeIntType(
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(false, hasLocale, false, false)

class AttributeDoubleType(
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(false, hasLocale, false, false)

class AttributeBoolType
    : AttributeType(false, false, false, false)

class AttributeTextType(
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(false, hasLocale, false, false)

class AttributeDateType(
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(false, hasLocale, hasPattern, false)

class AttributeDateTimeType(
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(false, hasLocale, hasPattern, false)

class AttributeStringListType(
    override val quote: String,
    override val delimiter: String
) : AttributeType(true, false, false, false)

class AttributeBoolListType(
    override val quote: String,
    override val delimiter: String
) : AttributeType(true, false, false, false)

class AttributeIntListType(
    override val quote: String,
    override val delimiter: String,
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(true, hasLocale, false, false)

class AttributeDoubleListType(
    override val quote: String,
    override val delimiter: String,
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(true, hasLocale, false, false)

class AttributeDateListType(
    override val quote: String,
    override val delimiter: String,
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(true, hasLocale, hasPattern, false)

class AttributeDateTimeListType(
    override val quote: String,
    override val delimiter: String,
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String
) : AttributeType(true, hasLocale, hasPattern, false)

class AttributeAttributeListType(
    override val quote: String,
    override val delimiter: String,
    override val valueDelimiter: String,
    override val valueQuote: String
) : AttributeType(false, false, false, true)