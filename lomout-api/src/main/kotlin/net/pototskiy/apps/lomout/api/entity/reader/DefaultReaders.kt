package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BOOLEANLIST
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATELIST
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DATETIMELIST
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.DOUBLELIST
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LONGLIST
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.type.Type
import kotlin.reflect.KClass

/**
 * Map of default readers
 */
val defaultReaders = mapOf<KClass<out Type>, AttributeReader<out Type>>(
    ATTRIBUTELIST::class to AttributeReaderWithPlugin(
        AttributeListReader::class
    ) {
        this as AttributeListReader
        quote = null
        delimiter = ','
        valueQuote = '"'
        valueDelimiter = '='
    },
    BOOLEAN::class to AttributeReaderWithPlugin(
        BooleanAttributeReader::class
    ) {
        this as BooleanAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    BOOLEANLIST::class to AttributeReaderWithPlugin(
        BooleanListAttributeReader::class
    ) {
        this as BooleanListAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    DATE::class to AttributeReaderWithPlugin(
        DateAttributeReader::class
    ) {
        this as DateAttributeReader
        pattern = "d.M.yy"
    },
    DATELIST::class to AttributeReaderWithPlugin(
        DateListAttributeReader::class
    ) {
        this as DateListAttributeReader
        pattern = "d.M.yy"
    },
    DATETIME::class to AttributeReaderWithPlugin(
        DateTimeAttributeReader::class
    ) {
        this as DateTimeAttributeReader
        pattern = "d.M.yy H:m"
    },
    DATETIMELIST::class to AttributeReaderWithPlugin(
        DateTimeListAttributeReader::class
    ) {
        this as DateTimeListAttributeReader
        pattern = "d.M.yy H:m"
    },
    DOUBLE::class to AttributeReaderWithPlugin(
        DoubleAttributeReader::class
    ) {
        this as DoubleAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    DOUBLELIST::class to AttributeReaderWithPlugin(
        DoubleListAttributeReader::class
    ) {
        this as DoubleListAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    LONG::class to AttributeReaderWithPlugin(
        LongAttributeReader::class
    ) {
        this as LongAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    LONGLIST::class to AttributeReaderWithPlugin(
        LongListAttributeReader::class
    ) {
        this as LongListAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    STRING::class to AttributeReaderWithPlugin(
        StringAttributeReader::class
    ),
    STRINGLIST::class to AttributeReaderWithPlugin(
        StringListAttributeReader::class
    ) {
        this as StringListAttributeReader
        quote = '"'
        delimiter = ','
    },
    TEXT::class to AttributeReaderWithPlugin(
        TextAttributeReader::class
    )
)
