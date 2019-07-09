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

import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet

/**
 * Row of data source excel file
 *
 * @property row Row
 * @property sheet Sheet
 * @property rowNum Int
 * @constructor
 */
class ExcelRow(private val row: org.apache.poi.ss.usermodel.Row) : Row {
    /**
     * Insert a new cell in row
     *
     * @param column Int
     * @return Cell
     */
    override fun insertCell(column: Int): Cell {
        return ExcelCell(row.createCell(column))
    }

    /**
     * Get cell by the index, or create empty if it does not exist
     *
     * @param column Int
     * @return Cell
     */
    override fun getOrEmptyCell(column: Int): Cell {
        return get(column) ?: ExcelCell(row.createCell(column))
    }

    override val sheet: Sheet
        get() = ExcelSheet(row.sheet)
    override val rowNum: Int
        get() = row.rowNum

    /**
     * Get cell by column number (index)
     *
     * @param column Int The column number
     * @return ExcelCell? Cell or null
     */
    override operator fun get(column: Int): ExcelCell? =
        row.getCell(column)?.let { ExcelCell(it) }

    /**
     * Get cell count in row
     *
     * @return Int
     */
    override fun countCell(): Int = if (row.lastCellNum.toInt() == -1) 0 else row.lastCellNum.toInt()

    /**
     * Get row cell iterator
     *
     * @return Iterator<ExcelCell>
     */
    override fun iterator(): Iterator<ExcelCell> =
        ExcelCellIterator(row)
}
