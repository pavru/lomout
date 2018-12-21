package net.pototskiy.apps.magemediation.config.excel

import javax.xml.bind.annotation.XmlEnum
import javax.xml.bind.annotation.XmlEnumValue
import javax.xml.bind.annotation.XmlType

@XmlType
@XmlEnum(String::class)
enum class FieldType(val isList: Boolean = false) {
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
    BOOL_LIST(true),
    @field:XmlEnumValue("int-list")
    INT_LIST(true),
    @field:XmlEnumValue("double-list")
    DOUBLE_LIST(true),
    @field:XmlEnumValue("string-list")
    STRING_LIST(true),
    @field:XmlEnumValue("date-list")
    DATE_LIST(true),
    @field:XmlEnumValue("datetime-list")
    DATETIME_LIST(true),
    @field:XmlEnumValue("attribute-list")
    ATTRIBUTE_LIST(true)
}
