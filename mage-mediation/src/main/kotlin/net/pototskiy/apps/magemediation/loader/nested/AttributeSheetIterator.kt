package net.pototskiy.apps.magemediation.loader.nested

class AttributeSheetIterator(private val workbook: AttributeWorkbook) : Iterator<AttributeSheet> {
    private var index = 0

    override fun hasNext(): Boolean = index < 1

    override fun next(): AttributeSheet = workbook[index++] as AttributeSheet

}