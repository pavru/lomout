package net.pototskiy.apps.lomout.api.source.nested

class NestedAttributeSheetIterator(private val workbook: NestedAttributeWorkbook) : Iterator<NestedAttributeSheet> {
    private var index = 0

    override fun hasNext(): Boolean = index < 1

    override fun next(): NestedAttributeSheet = workbook[index++] as NestedAttributeSheet
}
