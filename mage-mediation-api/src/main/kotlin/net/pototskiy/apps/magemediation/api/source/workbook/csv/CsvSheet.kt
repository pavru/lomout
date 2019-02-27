package net.pototskiy.apps.magemediation.api.source.workbook.csv

import net.pototskiy.apps.magemediation.api.source.workbook.Sheet
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import org.apache.commons.csv.CSVParser

class CsvSheet(
    private val _workbook: CsvWorkbook
) : Sheet {
    override val name: String
        get() = "default"
    override val workbook: Workbook
        get() = _workbook

    override fun get(row: Int): CsvRow {
        val iterator = _workbook.parser.iterator()
        var index = 0
        for (v in iterator) {
            if (index == row) {
                val data = mutableListOf<String>()
                for (s in v) {
                    data.add(s)
                }
                return CsvRow(index, data.toTypedArray(), this)
            }
            index++
        }
        throw SourceException("Index out of band")
    }

    override fun iterator(): Iterator<CsvRow> =
        CsvRowIterator(this)

    val parser: CSVParser
        get() = _workbook.parser
}
