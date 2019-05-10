package net.pototskiy.apps.lomout.api.source.workbook

import java.io.Closeable

/**
 * Source data workbook interface
 *
 * @property name String
 * @property type WorkbookType
 */
interface Workbook : Iterable<Sheet>, Closeable {
    /**
     * Workbook name
     */
    val name: String
    /**
     * Workbook type
     */
    val type: WorkbookType

    /**
     * Get sheet by name
     *
     * @param sheet String The sheet name
     * @return Sheet
     */
    operator fun get(sheet: String): Sheet

    /**
     * Get sheet by index
     *
     * @param sheet Int The sheet index, zero based
     * @return Sheet
     */
    operator fun get(sheet: Int): Sheet

    /**
     * Insert sheet into workbook
     *
     * @param sheet String The sheet name to insert
     * @return Sheet The inserted sheet
     */
    fun insertSheet(sheet: String): Sheet

    /**
     * Test if workbook has sheet with given name
     *
     * @param sheet String The sheet name
     * @return Boolean
     */
    fun hasSheet(sheet: String): Boolean
}
