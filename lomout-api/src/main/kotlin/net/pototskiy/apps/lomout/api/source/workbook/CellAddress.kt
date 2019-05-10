package net.pototskiy.apps.lomout.api.source.workbook

/**
 * Cell address
 *
 * @property row Int
 * @property column Int
 * @constructor
 */
data class CellAddress(
    /**
     * Cell row number, zero base
     */
    var row: Int,
    /**
     * Cell column number, zero base
     */
    var column: Int
)
