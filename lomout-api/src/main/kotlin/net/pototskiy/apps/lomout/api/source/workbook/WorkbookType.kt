package net.pototskiy.apps.lomout.api.source.workbook

/**
 * Workbook types
 */
enum class WorkbookType {
    /**
     * Workbook represents Excel file (xls, xlsx, xlsm)
     */
    EXCEL,
    /**
     * Workbook represents CSV file
     */
    CSV,
    /**
     * Workbook to process string with name-value pairs
     */
    ATTRIBUTE
}
