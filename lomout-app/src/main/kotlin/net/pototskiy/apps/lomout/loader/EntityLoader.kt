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

package net.pototskiy.apps.lomout.loader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.SuspectedLocation
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.document.DocumentData
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.document.emptyDocumentData
import net.pototskiy.apps.lomout.api.entity.reader
import net.pototskiy.apps.lomout.api.errorMessageFromException
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.script.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.script.loader.FieldSet
import net.pototskiy.apps.lomout.api.script.loader.Load
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.FieldAttributeMap
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.logging.log4j.LogManager
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class EntityLoader(
    private val loadConfig: Load<*>,
    private val emptyRowBehavior: EmptyRowBehavior,
    private val sheet: Sheet
) {

    private val repository = LomoutContext.getContext().repository
    private val logger = LogManager.getLogger(LOADER_LOG_NAME)
    var processedRows = 0L
    private val updateChanel = Channel<UpdaterData>(CHANEL_CAPACITY)

    private lateinit var updater: EntityUpdater
    private val extraData = mutableMapOf<String, DocumentData>()
    private var fieldSets = loadConfig.fieldSets
    private var eType = loadConfig.entity

    fun load() = runBlocking(LomoutContext.getContext().asCoroutineContext()) {
        val updaterJob = launch(Dispatchers.IO) {
            updateChanel.consumeEach { updateEntity(it) }
        }
        updater = EntityUpdater(eType)
        processRows()
        updateChanel.close()
        updaterJob.join()
        repository.markEntitiesAsRemoved(eType)
        repository.updateAbsentDays(eType)
        repository.removeOldEntities(eType, loadConfig.maxAbsentDays)
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun processRows() {
        loop@ for (row in sheet) {
            processedRows++
            if (row.rowNum == loadConfig.headersRow || row.rowNum < loadConfig.rowsToSkip) continue
            when (checkEmptyRow(row, emptyRowBehavior)) {
                EmptyRowTestResult.STOP -> break@loop
                EmptyRowTestResult.SKIP -> continue@loop
                EmptyRowTestResult.PROCESS -> { // assemble row }
                }
            }
            try {
                processRow(row)
            } catch (e: Exception) {
                AppDataException(suspectedLocation(row), message("message.error.loader.data.cannot_process_row"), e)
                    .errorMessageFromException(logger)
                continue
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun updateEntity(data: UpdaterData) {
        try {
            updater.update(data.data)
        } catch (e: Exception) {
            e.errorMessageFromException(logger)
        }
    }

    private suspend fun processRow(row: Row) {
        val rowFiledSet = findRowFieldSet(row)
        if (rowFiledSet.mainSet) {
            val data = getData(row, rowFiledSet.fieldToAttr)
            plusAdditionalData(data)
            validateKeyFieldData(data, rowFiledSet.fieldToAttr)
            updateChanel.send(UpdaterData(row, data, suspectedLocation(row) + loadConfig.entity))
        } else {
            val data = getData(row, rowFiledSet.fieldToAttr)
            extraData[rowFiledSet.name] = data
        }
    }

    private fun plusAdditionalData(data: DocumentData) =
        extraData.forEach { (_, gData) -> data.putAll(gData) }

    private fun checkEmptyRow(
        row: Row,
        emptyRowBehavior: EmptyRowBehavior
    ): EmptyRowTestResult {
        return if (row.countCell() == 0 || row.all { it == null || it.cellType == CellType.BLANK }) {
            when (emptyRowBehavior) {
                EmptyRowBehavior.STOP -> {
                    logger.info(
                        message("message.info.loader.stop_row"),
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.STOP
                }
                EmptyRowBehavior.IGNORE -> {
                    logger.info(
                        message("message.info.loader.skip_row"),
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.SKIP
                }
            }
        } else {
            EmptyRowTestResult.PROCESS
        }
    }

    private fun validateKeyFieldData(
        data: Map<Attribute, Any?>,
        fields: FieldAttributeMap
    ) {
        val keyFields = fields.filter { it.value.isKey }
        keyFields.forEach { (_, attr) ->
            val v = data[attr]
            if (v == null || (v is String && v.isBlank())) {
                throw AppDataException(suspectedLocation(attr), message("message.error.loader.data.empty_key"))
            }
        }
    }

    @Suppress("ComplexMethod", "ThrowsCount", "TooGenericExceptionCaught")
    private fun getData(row: Row, fields: FieldAttributeMap): DocumentData {
        val data: DocumentData = emptyDocumentData()

        fields.forEach { (field, attr) ->
            try {
                val cell = row[field.column]
                    ?: if (attr.isNullable) row.getOrEmptyCell(field.column) else null
                        ?: throw AppDataException(
                            suspectedLocation(row) + field + attr,
                            message("message.error.loader.data.no_cell")
                        )
                testFieldRegex(field, cell)
                @Suppress("UNCHECKED_CAST")
                (attr.reader as AttributeReader<Any?>)(attr, cell).also {
                    if (it == null && (!attr.isNullable || attr.isKey)) {
                        throw AppDataException(
                            suspectedLocation(attr) + field + cell + attr,
                            message("message.error.loader.data.null_to_notnull")
                        )
                    } else if (it != null) {
                        data[attr] = it
                    }
                }
            } catch (e: AppDataException) {
                throw AppDataException(e.suspectedLocation + field + attr, e.message)
            } catch (e: Exception) {
                throw AppDataException(suspectedLocation(field) + attr, e.message)
            }
        }
        return data
    }

    private fun testFieldRegex(field: Field, cell: Cell) {
        if (!field.isMatchToPattern(cell.asString())) {
            throw AppDataException(
                suspectedLocation(field) + cell,
                message("message.error.loader.data.field_not_match")
            )
        }
    }

    private fun findRowFieldSet(row: Row): FieldSet =
        (if (fieldSets.count() > 1) {
            var fittedSet: FieldSet? = null
            for (set in fieldSets) {
                if (testRowAgainstFieldSet(row, set)) {
                    fittedSet = set
                    break
                }
            }
            fittedSet
        } else {
            null
        } ?: fieldSets.mainSet)

    private fun testRowAgainstFieldSet(row: Row, set: FieldSet): Boolean {
        var fit = true
        for (field in set.fields.filter { field -> field.regex != null }) {
            val cell = row[field.column]
                ?: throw AppDataException(
                    suspectedLocation(field) + row,
                    message("message.error.loader.data.no_classifier_cell")
                )
            if (!field.isMatchToPattern(cell.asString())) fit = false
        }
        return fit
    }

    private data class UpdaterData(
        val row: Row,
        val data: DocumentData,
        val place: SuspectedLocation
    )

    companion object {
        private const val CHANEL_CAPACITY = 500
    }
}
