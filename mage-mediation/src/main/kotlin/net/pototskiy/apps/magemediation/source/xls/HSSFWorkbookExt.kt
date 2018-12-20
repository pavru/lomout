package net.pototskiy.apps.magemediation.source.xls

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.lang.ref.WeakReference

private data class WorkbookFile(
    val workbook: WeakReference<HSSFWorkbook>,
    val file: String
)

private val files = mutableListOf<WorkbookFile>()


fun HSSFWorkbook.setFileName(name: String) {
    cleanFiles()
    files.removeIf { it.workbook.get() == this }
    files.add(
        WorkbookFile(
            WeakReference(this),
            name
        )
    )
}

fun HSSFWorkbook.getFileName(): String {
    cleanFiles()
    return files.find { it.workbook.get() == this }?.file ?: ""
}
private fun cleanFiles() {
    files.removeIf { it.workbook.get() == null }
}