package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
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
 * Map of default writers
 */
val defaultWriters = mapOf<KClass<out Type>, AttributeWriter<out Type>>(
    ATTRIBUTELIST::class to AttributeWriterWithPlugin(
        AttributeListStringWriter::class
    ) {
        this as AttributeListStringWriter
        quote = null
        delimiter = ','
        valueQuote = '"'
        valueDelimiter = '='
    },
    BOOLEAN::class to AttributeWriterWithPlugin(
        BooleanAttributeStringWriter::class
    ) {
        this as BooleanAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    BOOLEANLIST::class to AttributeWriterWithPlugin(
        BooleanListAttributeStringWriter::class
    ) {
        this as BooleanListAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    DATE::class to AttributeWriterWithPlugin(
        DateAttributeStringWriter::class
    ) {
        this as DateAttributeStringWriter
        pattern = "d.M.yy"
    },
    DATELIST::class to AttributeWriterWithPlugin(
        DateListAttributeStringWriter::class
    ) {
        this as DateListAttributeStringWriter
        pattern = "d.M.yy"
    },
    DATETIME::class to AttributeWriterWithPlugin(
        DateTimeAttributeStringWriter::class
    ) {
        this as DateTimeAttributeStringWriter
        pattern = "d.M.yy H:m"
    },
    DATETIMELIST::class to AttributeWriterWithPlugin(
        DateTimeListAttributeStringWriter::class
    ) {
        this as DateTimeListAttributeStringWriter
        pattern = "d.M.yy H:m"
    },
    DOUBLE::class to AttributeWriterWithPlugin(
        DoubleAttributeStringWriter::class
    ) {
        this as DoubleAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    DOUBLELIST::class to AttributeWriterWithPlugin(
        DoubleListAttributeStringWriter::class
    ) {
        this as DoubleListAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    LONG::class to AttributeWriterWithPlugin(
        LongAttributeStringWriter::class
    ) {
        this as LongAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    LONGLIST::class to AttributeWriterWithPlugin(
        LongListAttributeStringWriter::class
    ) {
        this as LongListAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    STRING::class to AttributeWriterWithPlugin(
        StringAttributeStringWriter::class
    ),
    STRINGLIST::class to AttributeWriterWithPlugin(
        StringListAttributeStringWriter::class
    ) {
        this as StringListAttributeStringWriter
        quote = '"'
        delimiter = ','
    },
    TEXT::class to AttributeWriterWithPlugin(
        TextAttributeStringWriter::class
    )
)
