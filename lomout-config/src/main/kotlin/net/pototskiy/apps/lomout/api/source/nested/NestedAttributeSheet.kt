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

package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.suspectedLocation
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook

/**
 * Attribute workbook sheet
 *
 * @property backingWorkbook NestedAttributeWorkbook The sheet workbook
 * @property name String
 * @property workbook Workbook
 * @constructor
 */
class NestedAttributeSheet(
    private val backingWorkbook: NestedAttributeWorkbook
) : Sheet {
    /**
     * Insert row into sheet by the index
     *
     * @param row Int, The row index, zero based, only 0 and 1 are allowed
     * @return Row The inserted row
     */
    override fun insertRow(row: Int): Row = NestedAttributeRow(
        row,
        when (row) {
            0 -> (workbook as NestedAttributeWorkbook).cells[0]
            1 -> (workbook as NestedAttributeWorkbook).cells[1]
            else -> throw AppDataException(suspectedLocation(this), message("message.error.source.nested.only_2_rows"))
        },
        this
    )

    /**
     * Sheet name, always is [CSV_SHEET_NAME]
     */
    override val name: String
        get() = CSV_SHEET_NAME
    /**
     * Sheet workbook
     */
    override val workbook: Workbook
        get() = backingWorkbook

    /**
     * Get row by the index
     *
     * @param row Int The row index, only 0 and 1 is allowed
     * @return Row
     */
    override fun get(row: Int): Row = NestedAttributeRow(
        row,
        when (row) {
            0 -> (workbook as NestedAttributeWorkbook).cells[0]
            1 -> (workbook as NestedAttributeWorkbook).cells[1]
            else -> throw AppDataException(suspectedLocation(this), "Attribute workbook has only 2 rows.")
        },
        this
    )

    /**
     * Get workbook sheet iterator
     *
     * @return Iterator<Row>
     */
    override fun iterator(): Iterator<Row> = NestedAttributeRowIterator(this)
}
