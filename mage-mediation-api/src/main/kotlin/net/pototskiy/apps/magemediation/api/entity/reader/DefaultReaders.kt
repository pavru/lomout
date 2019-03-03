package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.AttributeReader
import net.pototskiy.apps.magemediation.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeType
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.TextType
import net.pototskiy.apps.magemediation.api.entity.Type
import kotlin.reflect.KClass

val defaultReaders = mapOf<KClass<out Type>, AttributeReader<out Type>>(
    AttributeListType::class to AttributeReaderWithPlugin(
        AttributeListReader::class
    ) {
        this as AttributeListReader
        quote = ""
        delimiter = ","
        valueQuote = "\""
        valueDelimiter = "="
    },
    BooleanType::class to AttributeReaderWithPlugin(
        BooleanAttributeReader::class
    ) {
        this as BooleanAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    BooleanListType::class to AttributeReaderWithPlugin(
        BooleanListAttributeReader::class
    ) {
        this as BooleanListAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    DateType::class to AttributeReaderWithPlugin(
        DateAttributeReader::class
    ) {
        this as DateAttributeReader
        pattern = "d.M.yy"
    },
    DateListType::class to AttributeReaderWithPlugin(
        DateListAttributeReader::class
    ) {
        this as DateListAttributeReader
        pattern = "d.M.yy"
    },
    DateTimeType::class to AttributeReaderWithPlugin(
        DateTimeAttributeReader::class
    ) {
        this as DateTimeAttributeReader
        pattern = "d.M.yy H:m"
    },
    DateTimeListType::class to AttributeReaderWithPlugin(
        DateTimeListAttributeReader::class
    ) {
        this as DateTimeListAttributeReader
        pattern = "d.M.yy H:m"
    },
    DoubleType::class to AttributeReaderWithPlugin(
        DoubleAttributeReader::class
    ) {
        this as DoubleAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    DoubleListType::class to AttributeReaderWithPlugin(
        DoubleListAttributeReader::class
    ) {
        this as DoubleListAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    LongType::class to AttributeReaderWithPlugin(
        LongAttributeReader::class
    ) {
        this as LongAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    LongListType::class to AttributeReaderWithPlugin(
        LongListAttributeReader::class
    ) {
        this as LongListAttributeReader
        locale = DEFAULT_LOCALE_STR
    },
    StringType::class to AttributeReaderWithPlugin(
        StringAttributeReader::class
    ),
    StringListType::class to AttributeReaderWithPlugin(
        StringListAttributeReader::class
    ) {
        this as StringListAttributeReader
        quote = "\""
        delimiter = ","
    },
    TextType::class to AttributeReaderWithPlugin(
        TextAttributeReader::class
    )
)
