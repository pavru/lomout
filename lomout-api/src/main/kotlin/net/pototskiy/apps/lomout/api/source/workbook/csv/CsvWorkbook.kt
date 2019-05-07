package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.AppSheetException
import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType
import java.io.File
import java.net.URL
import java.util.*

/**
 * CSV source file workbook
 *
 * @property workbookLocale Locale
 * @property sheet CsvSheet?
 * @property sourceURL URL
 * @property name String
 * @property type WorkbookType
 * @constructor
 */
abstract class CsvWorkbook(
    val workbookLocale: Locale = DEFAULT_LOCALE
) : Workbook {

    var sheet: CsvSheet? = null

    protected var sourceURL: URL = URL("file", "local", "virtual")

    /**
     * Workbook name (file name)
     */
    override val name: String
        get() = File(sourceURL.file).name // _fileName
    /**
     * Workbook type
     */
    override val type: WorkbookType
        get() = WorkbookType.CSV

    /**
     * Get sheet by name
     *
     * @param sheet String The sheet name
     * @return CsvSheet
     */
    override fun get(sheet: String): CsvSheet {
        if (sheet == CSV_SHEET_NAME) {
            return CsvSheet(this).also { this.sheet = it }
        } else {
            throw AppSheetException("CSV file supports only one sheet with name: \"default\"")
        }
    }

    /**
     * Get sheet by index
     *
     * @param sheet Int The sheet index, zero based
     * @return CsvSheet
     */
    override fun get(sheet: Int): CsvSheet {
        if (sheet == 0) {
            return CsvSheet(this).also { this.sheet = it }
        } else {
            throw AppSheetException("CSV file support only one sheet with index: 0")
        }
    }

    override fun hasSheet(sheet: String): Boolean {
        return sheet == CSV_SHEET_NAME
    }

    override fun iterator(): Iterator<CsvSheet> =
        CsvSheetIterator(this)
}
