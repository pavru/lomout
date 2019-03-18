package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType

class ExcelWorkbook<T : org.apache.poi.ss.usermodel.Workbook>(
    private val workbook: T,
    private val forInput: Boolean = true
) : Workbook {
    override val name: String
        get() = workbook.getFileName()
    override val type: WorkbookType
        get() = WorkbookType.EXCEL

    override operator fun get(sheet: String): ExcelSheet =
        ExcelSheet(workbook.getSheet(sheet))

    override operator fun get(sheet: Int): ExcelSheet =
        ExcelSheet(workbook.getSheetAt(sheet))

    override fun hasSheet(sheet: String): Boolean {
        return this.any { it.name == sheet }
    }

    override fun iterator(): Iterator<ExcelSheet> =
        ExcelSheetIterator(workbook)

    override fun close() {
        if (!forInput) {
            workbook.getFile()?.outputStream()?.use {
                workbook.write(it)
            }
        }
        workbook.close()
    }

    override fun insertSheet(sheet: String): Sheet {
        return ExcelSheet(workbook.createSheet(sheet))
    }
}
