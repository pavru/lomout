package net.pototskiy.apps.magemediation.source.csv

import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.source.Workbook
import net.pototskiy.apps.magemediation.source.WorkbookType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.net.URL


class CsvWorkbook(
    private val sourceURL: URL,
    private val csvFormat: CSVFormat
) : Workbook {

    private var reader = sourceURL.openStream().reader()//file.reader()
    private var _parser: CSVParser = csvFormat.parse(reader)

    override val name: String
        get() = File(sourceURL.file).name //_fileName
    override val type: WorkbookType
        get() = WorkbookType.CSV

    override fun get(sheet: String): CsvSheet {
        if (sheet == "default") {
            return CsvSheet(this)
        } else {
            throw LoaderException("CSV file supports only one sheet with name: \"default\"")
        }
    }

    override fun get(sheet: Int): CsvSheet {
        if (sheet == 0) {
            return CsvSheet(this)
        } else {
            throw LoaderException("CSV file support only one sheet with index: 0")
        }
    }

    override fun hasSheet(sheet: String): Boolean {
        return sheet == "default"
    }

    override fun iterator(): Iterator<CsvSheet> = CsvSheetIterator(this)

    val parser: CSVParser
        get() = _parser

    fun reset() {
        reader.close()
        reader = sourceURL.openStream().reader()
        _parser = csvFormat.parse(reader)
    }
}