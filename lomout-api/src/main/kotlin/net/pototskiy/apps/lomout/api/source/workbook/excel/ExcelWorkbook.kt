package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType

/**
 * Excel workbook source file
 *
 * @param T Workbook implementation type
 * @property workbook POI workbook
 * @property forInput Read only workbook
 * @property name Workbook name
 * @property type Workbook type
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
     * Get workbook sheet by the index
     *
     * @param sheet Int The sheet index, zero based
     * @return ExcelSheet Sheet
     */
    override operator fun get(sheet: Int): ExcelSheet =
        ExcelSheet(workbook.getSheetAt(sheet))

    /**
     * Check if workbook has a sheet
     *
     * @param sheet String The sheet name
     * @return Boolean true — sheet exists, false — no sheet in the workbook
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
     * Insert a new sheet into the workbook
     *
     * @param sheet The sheet name
     * @return Sheet
     */
    override fun insertSheet(sheet: String): Sheet {
        return ExcelSheet(workbook.createSheet(sheet))
    }
}
