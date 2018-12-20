package net.pototskiy.apps.magemediation.source.xlsx

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.lang.ref.WeakReference

private data class WorkbookFile(
    val workbook: WeakReference<XSSFWorkbook>,
    val file: String
)

private val files = mutableListOf<WorkbookFile>()


public fun XSSFWorkbook.setFileName(name: String) {
    cleanFiles()
    files.removeIf { it.workbook.get() == this }
    files.add(
        WorkbookFile(
            WeakReference(this),
            name
        )
    )
}

public fun XSSFWorkbook.getFileName(): String {
    cleanFiles()
    return files.find { it.workbook.get() == this }?.file ?: ""
}
private fun cleanFiles() {
    files.removeIf { it.workbook.get() == null }
}