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

package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook

/**
 * Excel file sheet
 *
 * @property sheet Sheet
 * @property name String
 * @property workbook Workbook
 * @constructor
 */
class ExcelSheet(private val sheet: org.apache.poi.ss.usermodel.Sheet) : Sheet {
    /**
     * Insert new row to sheet
     *
     * @param row Int The row number to insert
     * @return Row Inserted row
     */
    override fun insertRow(row: Int): Row {
        return ExcelRow(sheet.createRow(row))
    }

    override val name: String
        get() = sheet.sheetName
    override val workbook: Workbook
        get() = ExcelWorkbook(sheet.workbook)

    /**
     * Get sheet row by the index
     *
     * @param row Int The row number (index)
     * @return ExcelRow? The row or null
     */
    override operator fun get(row: Int): ExcelRow? =
        sheet.getRow(row)?.let { ExcelRow(it) }

    /**
     * Get row iterator
     *
     * @return Iterator<ExcelRow>
     */
    override fun iterator(): Iterator<ExcelRow> =
        ExcelRowIterator(sheet)
}
