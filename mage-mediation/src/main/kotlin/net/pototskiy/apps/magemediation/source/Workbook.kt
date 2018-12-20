package net.pototskiy.apps.magemediation.source

interface Workbook: Iterable<Sheet> {
    val name: String
    val type: WorkbookType
    operator fun get(sheet: String): Sheet
    operator fun get(sheet: Int): Sheet
    fun hasSheet(sheet: String): Boolean
}