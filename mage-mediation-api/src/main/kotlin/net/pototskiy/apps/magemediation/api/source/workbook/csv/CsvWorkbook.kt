package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.CSV_SHEET_NAME
import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.source.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType
import java.io.File
import java.net.URL
import java.util.*

abstract class CsvWorkbook(
    val workbookLocale: Locale = DEFAULT_LOCALE
) : Workbook {

    var sheet: CsvSheet? = null

    private var sourceURL: URL = URL("file", "local", "virtual")

    override val name: String
        get() = File(sourceURL.file).name // _fileName
    override val type: WorkbookType
        get() = WorkbookType.CSV

    override fun get(sheet: String): CsvSheet {
        if (sheet == CSV_SHEET_NAME) {
            return CsvSheet(this).also { this.sheet = it }
        } else {
            throw SourceException("CSV file supports only one sheet with name: \"default\"")
        }
    }

    override fun get(sheet: Int): CsvSheet {
        if (sheet == 0) {
            return CsvSheet(this).also { this.sheet = it }
        } else {
            throw SourceException("CSV file support only one sheet with index: 0")
        }
    }

    override fun hasSheet(sheet: String): Boolean {
        return sheet == CSV_SHEET_NAME
    }

    override fun iterator(): Iterator<CsvSheet> =
        CsvSheetIterator(this)
}
