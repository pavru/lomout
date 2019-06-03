package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import org.apache.commons.csv.CSVParser

/**
 * CSV workbook sheet
 *
 * @property backingWorkbook CsvWorkbook
 * @property lastCreatedRow CsvRow?
 * @property name String
 * @property workbook Workbook
 * @property parser CSVParser
 * @constructor
 */
class CsvSheet(
    private val backingWorkbook: CsvWorkbook
) : Sheet {
    private var lastCreatedRow: CsvRow? = null

    /**
     * Get sheet name
     */
    override val name: String
        get() = CSV_SHEET_NAME
    /**
     * Get sheet workbook
     */
    override val workbook: Workbook
        get() = backingWorkbook

    /**
     * Get sheet row by the index, zero based
     *
     * @param row Int
     * @return CsvRow
     */
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
        throw AppDataException(badPlace(this), "Index out of band.")
    }

    /**
     * Insert row in sheet by the index, zero based
     *
     * @param row Int The row index
     * @return Row The inserted row
     */
    override fun insertRow(row: Int): Row {
        checkThatItIsCsvOutputWorkbook(backingWorkbook)
        writeLastRow()
        return CsvRow(0, null, this).also {
            lastCreatedRow = it
        }
    }

    /**
     * Flush last sheet row to file
     */
    fun writeLastRow() {
        checkThatItIsCsvOutputWorkbook(backingWorkbook)
        lastCreatedRow?.let { lastRow ->
            backingWorkbook.printer.printRecord(lastRow.map { it?.stringValue ?: "" })
        }
    }

    /**
     * Get row iterator
     *
     * @return Iterator<CsvRow>
     */
    override fun iterator(): Iterator<CsvRow> =
        CsvRowIterator(this)

    /**
     * Get CSVParser
     */
    val parser: CSVParser
        get() {
            checkThatItIsCsvInputWorkbook(backingWorkbook)
            return backingWorkbook.parser
        }
}
