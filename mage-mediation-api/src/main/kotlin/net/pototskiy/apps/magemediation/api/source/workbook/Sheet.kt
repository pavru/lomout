package net.pototskiy.apps.magemediation.api.source.workbook

interface Sheet: Iterable<Row> {
    val name: String
    val workbook: Workbook
    operator fun get(row: Int): Row?
}
