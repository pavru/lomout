package net.pototskiy.apps.magemediation.source

enum class SourceFieldType(val isList: Boolean = false) {
    BOOL,
    INT,
    DOUBLE,
    STRING,
    TEXT,
    DATE,
    DATETIME,
    BOOL_LIST(true),
    INT_LIST(true),
    DOUBLE_LIST(true),
    STRING_LIST(true),
    DATE_LIST(true),
    DATETIME_LIST(true),
    ATTRIBUTE_LIST(true)
}