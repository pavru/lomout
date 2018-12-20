package net.pototskiy.apps.magemediation.source

interface Sheet: Iterable<Row> {
    val name: String
    val workbook: Workbook
    operator fun get(row: Int): Row
}