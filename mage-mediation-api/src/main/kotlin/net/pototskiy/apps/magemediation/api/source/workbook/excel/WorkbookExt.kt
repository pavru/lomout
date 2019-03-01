package net.pototskiy.apps.magemediation.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Workbook
import java.lang.ref.WeakReference

private data class WorkbookFile(
    val workbook: WeakReference<Workbook>,
    val file: String
)

private val files = mutableListOf<WorkbookFile>()

fun Workbook.setFileName(name: String) {
    cleanFiles()
    files.removeIf { it.workbook.get() == this }
    files.add(
        WorkbookFile(
            WeakReference(this),
            name
        )
    )
}

fun Workbook.getFileName(): String {
    cleanFiles()
    return files.find { it.workbook.get() == this }?.file ?: ""
}

private fun cleanFiles() {
    files.removeIf { it.workbook.get() == null }
}
