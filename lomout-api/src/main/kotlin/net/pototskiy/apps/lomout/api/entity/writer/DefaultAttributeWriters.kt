package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.lomout.api.entity.BooleanListType
import net.pototskiy.apps.lomout.api.entity.BooleanType
import net.pototskiy.apps.lomout.api.entity.DateListType
import net.pototskiy.apps.lomout.api.entity.DateTimeListType
import net.pototskiy.apps.lomout.api.entity.DateTimeType
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.entity.DoubleListType
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.entity.LongListType
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.StringListType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.TextType
import net.pototskiy.apps.lomout.api.entity.Type
import kotlin.reflect.KClass

val defaultWriters = mapOf<KClass<out Type>, AttributeWriter<out Type>>(
    AttributeListType::class to AttributeWriterWithPlugin(
        AttributeListStringWriter::class
    ) {
        this as AttributeListStringWriter
        quote = null
        delimiter = ','
        valueQuote = '"'
        valueDelimiter = '='
    },
    BooleanType::class to AttributeWriterWithPlugin(
        BooleanAttributeStringWriter::class
    ) {
        this as BooleanAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    BooleanListType::class to AttributeWriterWithPlugin(
        BooleanListAttributeStringWriter::class
    ) {
        this as BooleanListAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    DateType::class to AttributeWriterWithPlugin(
        DateAttributeStringWriter::class
    ) {
        this as DateAttributeStringWriter
        pattern = "d.M.yy"
    },
    DateListType::class to AttributeWriterWithPlugin(
        DateListAttributeStringWriter::class
    ) {
        this as DateListAttributeStringWriter
        pattern = "d.M.yy"
    },
    DateTimeType::class to AttributeWriterWithPlugin(
        DateTimeAttributeStringWriter::class
    ) {
        this as DateTimeAttributeStringWriter
        pattern = "d.M.yy H:m"
    },
    DateTimeListType::class to AttributeWriterWithPlugin(
        DateTimeListAttributeStringWriter::class
    ) {
        this as DateTimeListAttributeStringWriter
        pattern = "d.M.yy H:m"
    },
    DoubleType::class to AttributeWriterWithPlugin(
        DoubleAttributeStringWriter::class
    ) {
        this as DoubleAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    DoubleListType::class to AttributeWriterWithPlugin(
        DoubleListAttributeStringWriter::class
    ) {
        this as DoubleListAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    LongType::class to AttributeWriterWithPlugin(
        LongAttributeStringWriter::class
    ) {
        this as LongAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    LongListType::class to AttributeWriterWithPlugin(
        LongListAttributeStringWriter::class
    ) {
        this as LongListAttributeStringWriter
        locale = DEFAULT_LOCALE_STR
    },
    StringType::class to AttributeWriterWithPlugin(
        StringAttributeStringWriter::class
    ),
    StringListType::class to AttributeWriterWithPlugin(
        StringListAttributeStringWriter::class
    ) {
        this as StringListAttributeStringWriter
        quote = '"'
        delimiter = ','
    },
    TextType::class to AttributeWriterWithPlugin(
        TextAttributeStringWriter::class
    )
)
