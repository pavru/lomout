package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.booleanListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.booleanType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.datetimeListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateTimeType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.documentType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.doubleListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.doubleType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.intListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.intType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.longListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.longType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.stringListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.stringType

/**
 * Map of default writers
 */
val defaultWriters = mapOf(
    documentType to DocumentAttributeStringWriter().apply {
        quote = null
        delimiter = ','
        valueQuote = '"'
        valueDelimiter = '='
    },
    booleanType to BooleanAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    booleanListType to BooleanListAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    dateType to DateAttributeStringWriter().apply {
        pattern = "d.M.uu"
    },
    dateListType to DateListAttributeStringWriter().apply {
        pattern = "d.M.uu"
    },
    dateTimeType to DateTimeAttributeStringWriter().apply {
        pattern = "d.M.uu H:m"
    },
    datetimeListType to DateTimeListAttributeStringWriter().apply {
        pattern = "d.M.uu H:m"
    },
    doubleType to DoubleAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    doubleListType to DoubleListAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    longType to LongAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    longListType to LongListAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    intType to IntAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    intListType to IntListAttributeStringWriter().apply {
        locale = DEFAULT_LOCALE_STR
    },
    stringType to StringAttributeStringWriter(),
    stringListType to StringListAttributeStringWriter().apply {
        quote = '"'
        delimiter = ','
    }
)
