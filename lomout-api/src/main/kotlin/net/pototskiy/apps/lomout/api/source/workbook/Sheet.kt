package net.pototskiy.apps.lomout.api.source.workbook

interface Sheet : Iterable<Row> {
    val name: String
    val workbook: Workbook
    operator fun get(row: Int): Row?
    fun insertRow(row: Int): Row
}
