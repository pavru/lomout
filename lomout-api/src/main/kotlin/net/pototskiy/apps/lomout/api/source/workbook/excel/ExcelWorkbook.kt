package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType

/**
 * Excel workbook source file
 *
 * @param T : org.apache.poi.ss.usermodel.Workbook
 * @property workbook T
 * @property forInput Boolean
 * @property name String
 * @property type WorkbookType
 * @constructor
 */
class ExcelWorkbook<T : org.apache.poi.ss.usermodel.Workbook>(
    private val workbook: T,
    private val forInput: Boolean = true
) : Workbook {
    override val name: String
        get() = workbook.getFileName()
    override val type: WorkbookType
        get() = WorkbookType.EXCEL

    /**
     * Get workbook sheet by name
     *
     * @param sheet String The sheet name
     * @return ExcelSheet
     */
    override operator fun get(sheet: String): ExcelSheet =
        ExcelSheet(workbook.getSheet(sheet))

    /**
     * Get workbook sheet by index
     *
     * @param sheet Int The sheet index, zero based
     * @return ExcelSheet Sheet
     */
    override operator fun get(sheet: Int): ExcelSheet =
        ExcelSheet(workbook.getSheetAt(sheet))

    /**
     * Check if workbook has sheet
     *
     * @param sheet String The sheet name
     * @return Boolean true - sheet exists, false - no sheet in workbook
     */
    override fun hasSheet(sheet: String): Boolean {
        return this.any { it.name == sheet }
    }

    /**
     * Get workbook sheet iterator
     *
     * @return Iterator<ExcelSheet>
     */
    override fun iterator(): Iterator<ExcelSheet> =
        ExcelSheetIterator(workbook)

    /**
     * Close workbook
     */
    override fun close() {
        if (!forInput) {
            workbook.getFile()?.outputStream()?.use {
                workbook.write(it)
            }
        }
        workbook.close()
    }

    /**
     * Insert new sheet into excel workbook
     *
     * @param sheet String
     * @return Sheet
     */
    override fun insertSheet(sheet: String): Sheet {
        return ExcelSheet(workbook.createSheet(sheet))
    }
}
