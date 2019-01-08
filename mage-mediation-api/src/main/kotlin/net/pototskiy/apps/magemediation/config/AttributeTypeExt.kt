package net.pototskiy.apps.magemediation.config

import net.pototskiy.apps.magemediation.config.type.*
import net.pototskiy.apps.magemediation.source.SourceFieldType

fun AttributeType.toSourceFieldType(): SourceFieldType = when (this) {
    is AttributeStringType -> SourceFieldType.STRING
    is AttributeIntType -> SourceFieldType.INT
    is AttributeDoubleType -> SourceFieldType.DOUBLE
    is AttributeBoolType -> SourceFieldType.BOOL
    is AttributeTextType -> SourceFieldType.TEXT
    is AttributeDateType -> SourceFieldType.DATE
    is AttributeDateTimeType -> SourceFieldType.DATETIME
    is AttributeStringListType -> SourceFieldType.STRING_LIST
    is AttributeBoolListType -> SourceFieldType.BOOL_LIST
    is AttributeIntListType -> SourceFieldType.INT_LIST
    is AttributeDoubleListType -> SourceFieldType.DOUBLE_LIST
    is AttributeDateListType -> SourceFieldType.DATE_LIST
    is AttributeDateTimeListType -> SourceFieldType.DATETIME_LIST
    is AttributeAttributeListType -> SourceFieldType.ATTRIBUTE_LIST
}
