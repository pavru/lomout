package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.source.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.net.URL
import java.util.*


class CsvWorkbook(
    private val sourceURL: URL,
    private val csvFormat: CSVFormat,
    val workbookLocale: Locale = DEFAULT_LOCALE
) : Workbook {

    private var reader = sourceURL.openStream().reader()//file.plugins.reader()
    private var _parser: CSVParser = csvFormat.parse(reader)

    override val name: String
        get() = File(sourceURL.file).name //_fileName
    override val type: WorkbookType
        get() = WorkbookType.CSV

    override fun get(sheet: String): CsvSheet {
        if (sheet == "default") {
            return CsvSheet(this)
        } else {
            throw SourceException("CSV file supports only one sheet with name: \"default\"")
        }
    }

    override fun get(sheet: Int): CsvSheet {
        if (sheet == 0) {
            return CsvSheet(this)
        } else {
            throw SourceException("CSV file support only one sheet with index: 0")
        }
    }

    override fun hasSheet(sheet: String): Boolean {
        return sheet == "default"
    }

    override fun iterator(): Iterator<CsvSheet> =
        CsvSheetIterator(this)

    val parser: CSVParser
        get() = _parser

    fun reset() {
        reader.close()
        reader = sourceURL.openStream().reader()
        _parser = csvFormat.parse(reader)
    }

    override fun close() {
        _parser.close()
        reader.close()
    }
}
