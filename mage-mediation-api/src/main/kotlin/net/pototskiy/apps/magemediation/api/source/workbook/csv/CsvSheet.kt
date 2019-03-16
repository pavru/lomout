package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.AppSheetException
import net.pototskiy.apps.magemediation.api.CSV_SHEET_NAME
import net.pototskiy.apps.magemediation.api.source.workbook.Row
import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import org.apache.commons.csv.CSVParser

class CsvSheet(
    private val backingWorkbook: CsvWorkbook
) : Sheet {
    private var lastCreatedRow: CsvRow? = null

    override val name: String
        get() = CSV_SHEET_NAME
    override val workbook: Workbook
        get() = backingWorkbook

    override fun get(row: Int): CsvRow {
        checkThatItIsCsvInputWorkbook(backingWorkbook)
        val iterator = backingWorkbook.parser.iterator()
        var index = 0
        for (v in iterator) {
            if (index == row) {
                return CsvRow(index, v, this)
            }
            index++
        }
        throw AppSheetException("Index out of band")
    }

    override fun insertRow(row: Int): Row {
        checkThatItIsCsvOutputWorkbook(backingWorkbook)
        writeLastRow()
        return CsvRow(0, null, this).also {
            lastCreatedRow = it
        }
    }

    fun writeLastRow() {
        checkThatItIsCsvOutputWorkbook(backingWorkbook)
        lastCreatedRow?.let { lastRow ->
            backingWorkbook.printer.printRecord(lastRow.map { it?.stringValue ?: "" })
        }
    }

    override fun iterator(): Iterator<CsvRow> =
        CsvRowIterator(this)

    val parser: CSVParser
        get() {
            checkThatItIsCsvInputWorkbook(backingWorkbook)
            return backingWorkbook.parser
        }
}
