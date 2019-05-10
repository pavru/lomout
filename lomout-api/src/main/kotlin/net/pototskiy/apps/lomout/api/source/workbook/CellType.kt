package net.pototskiy.apps.lomout.api.source.workbook

/**
 * Cell value types
 */
enum class CellType {
    /**
     * Long value
     */
    LONG,
    /**
     * Double value
     */
    DOUBLE,
    /**
     * Boolean value
     */
    BOOL,
    /**
     * String value
     */
    STRING,
    /**
     * Cell has no value
     */
    BLANK
}
