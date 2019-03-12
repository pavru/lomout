package net.pototskiy.apps.magemediation.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

private data class WorkbookFile(
    val workbook: WeakReference<Workbook>,
    val file: File
)

private val files = Collections.synchronizedList(mutableListOf<WorkbookFile>())

fun Workbook.setFileName(file: File) {
    cleanFiles()
    files.removeIf { it.workbook.get() == this }
    files.add(
        WorkbookFile(
            WeakReference(this),
            file
        )
    )
}

fun Workbook.getFileName(): String {
    cleanFiles()
    return files.find { it.workbook.get() == this }?.file?.name ?: ""
}
fun Workbook.getFile(): File? {
    cleanFiles()
    return files.find { it.workbook.get() == this }?.file
}

private fun cleanFiles() {
    files.removeIf { it.workbook.get() == null }
}
