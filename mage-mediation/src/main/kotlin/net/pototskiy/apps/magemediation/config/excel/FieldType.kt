package net.pototskiy.apps.magemediation.config.excel

import net.pototskiy.apps.magemediation.source.SourceFieldType
import javax.xml.bind.annotation.XmlEnum
import javax.xml.bind.annotation.XmlEnumValue
import javax.xml.bind.annotation.XmlType

@XmlType
@XmlEnum(String::class)
enum class FieldType {
    @field:XmlEnumValue("bool")
    BOOL,
    @field:XmlEnumValue("int")
    INT,
    @field:XmlEnumValue("double")
    DOUBLE,
    @field:XmlEnumValue("string")
    STRING,
    @field:XmlEnumValue("text")
    TEXT,
    @field:XmlEnumValue("date")
    DATE,
    @field:XmlEnumValue("datetime")
    DATETIME,
    @field:XmlEnumValue("bool-list")
    BOOL_LIST,
    @field:XmlEnumValue("int-list")
    INT_LIST,
    @field:XmlEnumValue("double-list")
    DOUBLE_LIST,
    @field:XmlEnumValue("string-list")
    STRING_LIST,
    @field:XmlEnumValue("date-list")
    DATE_LIST,
    @field:XmlEnumValue("datetime-list")
    DATETIME_LIST,
    @field:XmlEnumValue("attribute-list")
    ATTRIBUTE_LIST;

    fun toSourceFieldType(): SourceFieldType = when (this) {
        BOOL -> SourceFieldType.BOOL
        INT -> SourceFieldType.INT
        DOUBLE -> SourceFieldType.DOUBLE
        STRING -> SourceFieldType.STRING
        TEXT -> SourceFieldType.TEXT
        DATE -> SourceFieldType.DATE
        DATETIME -> SourceFieldType.DATETIME
        BOOL_LIST -> SourceFieldType.BOOL_LIST
        INT_LIST -> SourceFieldType.INT_LIST
        DOUBLE_LIST -> SourceFieldType.DOUBLE_LIST
        STRING_LIST -> SourceFieldType.STRING_LIST
        DATE_LIST -> SourceFieldType.DATE_LIST
        DATETIME_LIST -> SourceFieldType.DATETIME_LIST
        ATTRIBUTE_LIST -> SourceFieldType.ATTRIBUTE_LIST
    }
}
