package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection
import net.pototskiy.apps.lomout.api.config.loader.SourceData
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import java.io.Closeable

class EntityPrinter(
    file: SourceData,
    private val fieldSets: FieldSetCollection,
    printHead: Boolean
) : Closeable {
    private var lastRow = 0
    private val mainFields = fieldSets.mainSet.fields.sortedBy { it.column }
    private val extraFields = mutableMapOf<String, Map<AnyTypeAttribute, Type?>>()
    private val workbook = WorkbookFactory.create(
        file.file.file.toURI().toURL(),
        file.file.locale,
        false
    )

    private val sheet: Sheet

    init {
        sheet = workbook.insertSheet(file.sheet.name!!)
        if (printHead) {
            val row = sheet.insertRow(lastRow++)
            mainFields.forEachIndexed { c, field ->
                val cell = row.insertCell(c)
                cell.setCellValue(field.name)
            }
        }
    }

    fun print(data: Map<AnyTypeAttribute, Type?>): Long {
        val currentRow = lastRow
        printExtraFields(data)
        val row = sheet.insertRow(lastRow++)
        mainFields.forEachIndexed { c, field ->
            val cell = row.insertCell(c)
            val attr = fieldSets.mainSet.fieldToAttr[field]
            @Suppress("UNCHECKED_CAST")
            (attr?.writer as? AttributeWriter<Type>)?.write(data[attr], cell)
        }
        return (lastRow - currentRow).toLong()
    }

    private fun printExtraFields(data: Map<AnyTypeAttribute, Type?>) {
        fieldSets.filter { !it.mainSet }.forEach { fields ->
            val extData = data.filter { it.key in fields.fieldToAttr.values }
            val lastData = extraFields[fields.name]
            if (lastData == null || extData.any { it.value?.value != lastData[it.key]?.value }) {
                val row = sheet.insertRow(lastRow++)
                fields.fields.sortedBy { it.column }.forEachIndexed { c, field ->
                    val cell = row.insertCell(c)
                    val attr = fields.fieldToAttr[field]
                    @Suppress("UNCHECKED_CAST")
                    (attr?.writer as? AttributeWriter<Type>)?.write(extData[attr], cell)
                }
                extraFields[fields.name] = extData
            }
        }
    }

    override fun close() {
        workbook.close()
    }
}
