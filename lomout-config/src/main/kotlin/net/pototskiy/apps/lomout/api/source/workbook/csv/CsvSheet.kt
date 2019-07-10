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

package net.pototskiy.apps.lomout.api.source.workbook.csv

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import org.apache.commons.csv.CSVParser

/**
 * CSV workbook sheet
 *
 * @property backingWorkbook CsvWorkbook
 * @property lastCreatedRow CsvRow?
 * @property name String
 * @property workbook Workbook
 * @property parser CSVParser
 * @constructor
 */
class CsvSheet(
    private val backingWorkbook: CsvWorkbook
) : Sheet {
    private var lastCreatedRow: CsvRow? = null

    /**
     * Get sheet name
     */
    override val name: String
        get() = CSV_SHEET_NAME
    /**
     * Get sheet workbook
     */
    override val workbook: Workbook
        get() = backingWorkbook

    /**
     * Get sheet row by the index, zero based
     *
     * @param row Int
     * @return CsvRow
     */
    override fun get(row: Int): CsvRow {
        checkThatItIsCsvInputWorkbook(backingWorkbook)
        val iterator = backingWorkbook.parser.iterator()
        var index = 0
        for (v in iterator) {
            if (index == row) {
                return CsvRow(index, v, this)
            }
            index++
        }
        throw AppDataException(badPlace(this), "Index out of band.")
    }

    /**
     * Insert row in sheet by the index, zero based
     *
     * @param row Int The row index
     * @return Row The inserted row
     */
    override fun insertRow(row: Int): Row {
        checkThatItIsCsvOutputWorkbook(backingWorkbook)
        writeLastRow()
        return CsvRow(0, null, this).also {
            lastCreatedRow = it
        }
    }

    /**
     * Flush last sheet row to file
     */
    fun writeLastRow() {
        checkThatItIsCsvOutputWorkbook(backingWorkbook)
        lastCreatedRow?.let { lastRow ->
            backingWorkbook.printer.printRecord(lastRow.map { it?.stringValue ?: "" })
        }
    }

    /**
     * Get row iterator
     *
     * @return Iterator<CsvRow>
     */
    override fun iterator(): Iterator<CsvRow> =
        CsvRowIterator(this)

    /**
     * Get CSVParser
     */
    val parser: CSVParser
        get() {
            checkThatItIsCsvInputWorkbook(backingWorkbook)
            return backingWorkbook.parser
        }
}
