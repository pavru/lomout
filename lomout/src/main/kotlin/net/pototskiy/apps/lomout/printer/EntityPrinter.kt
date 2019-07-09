/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection
import net.pototskiy.apps.lomout.api.config.loader.SourceData
import net.pototskiy.apps.lomout.api.config.loader.SourceSheetDefinition.SourceSheetDefinitionWithName
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.writer
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
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
    private val extraFields = mutableMapOf<String, Map<Attribute, Any?>>()
    private val workbook = WorkbookFactory.create(
        file.file.file.toURI().toURL(),
        file.file.locale,
        false
    )

    private val sheet: Sheet

    init {
        sheet = workbook.insertSheet((file.sheet as SourceSheetDefinitionWithName).name)
        if (printHead) {
            val row = sheet.insertRow(lastRow++)
            mainFields.forEachIndexed { c, field ->
                val cell = row.insertCell(c)
                cell.setCellValue(field.name)
            }
        }
    }

    fun print(data: Map<Attribute, Any?>): Long {
        val currentRow = lastRow
        printExtraFields(data)
        val row = sheet.insertRow(lastRow++)
        mainFields.forEachIndexed { c, field ->
            val cell = row.insertCell(c)
            val attr = fieldSets.mainSet.fieldToAttr[field]
            @Suppress("UNCHECKED_CAST")
            (attr?.writer as? AttributeWriter<Any?>)?.write(data[attr], cell)
        }
        return (lastRow - currentRow).toLong()
    }

    private fun printExtraFields(data: Map<Attribute, Any?>) {
        fieldSets.filter { !it.mainSet }.forEach { fields ->
            val extData = data.filter { it.key in fields.fieldToAttr.values }
            val lastData = extraFields[fields.name]
            if (lastData == null || extData.any { it.value != lastData[it.key] }) {
                val row = sheet.insertRow(lastRow++)
                fields.fields.sortedBy { it.column }.forEachIndexed { c, field ->
                    val cell = row.insertCell(c)
                    val attr = fields.fieldToAttr[field]
                    @Suppress("UNCHECKED_CAST")
                    (attr?.writer as? AttributeWriter<Any?>)?.write(extData[attr], cell)
                }
                extraFields[fields.name] = extData
            }
        }
    }

    override fun close() {
        workbook.close()
    }
}
