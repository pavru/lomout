package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.AppWorkbookException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
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

    constructor(source: URL, csvFormat: CSVFormat, workbookLocale: Locale = DEFAULT_LOCALE)
            : this(source.openStream().reader(), csvFormat, workbookLocale) {
        this.sourceURL = source
    }

    private var _parser: CSVParser = csvFormat.parse(reader)

    override fun insertSheet(sheet: String): Sheet {
        throw AppWorkbookException("CSV input workbook does not support sheet insertion")
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
        throw AppWorkbookException("CSV workbook is not input one")
    }
}
