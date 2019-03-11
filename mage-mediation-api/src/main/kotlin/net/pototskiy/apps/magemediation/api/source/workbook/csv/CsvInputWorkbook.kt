package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.source.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import kotlin.contracts.contract

class CsvInputWorkbook(
    private val reader: InputStreamReader,
    csvFormat: CSVFormat,
    workbookLocale: Locale = DEFAULT_LOCALE
) : CsvWorkbook(workbookLocale) {
    private var sourceURL: URL = URL("file", "local", "virtual")

    constructor(sourceURL: URL, csvFormat: CSVFormat, workbookLocale: Locale = DEFAULT_LOCALE)
            : this(sourceURL.openStream().reader(), csvFormat, workbookLocale) {
        this.sourceURL = sourceURL
    }

    private var _parser: CSVParser = csvFormat.parse(reader)

    override fun insertSheet(sheet: String): Sheet {
        throw SourceException("CSV input workbook does not support sheet insertion")
    }

    val parser: CSVParser
        get() = _parser

    override fun close() {
        _parser.close()
        reader.close()
    }
}

fun checkThatItIsCsvInputWorkbook(workbook: CsvWorkbook) {
    contract {
        returns() implies (workbook is CsvInputWorkbook)
    }
    if (workbook !is CsvInputWorkbook) {
        throw SourceException("CSV workbook is not input one")
    }
}
