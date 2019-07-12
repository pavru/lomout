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

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.entity.values.toDate
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.suspectedLocation
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Excel source cell
 *
 * @property cell Cell
 * @property address CellAddress
 * @property cellType CellType
 * @property booleanValue Boolean
 * @property longValue Long
 * @property doubleValue Double
 * @property stringValue String
 * @property row Row
 * @constructor
 */
class ExcelCell(private val cell: org.apache.poi.ss.usermodel.Cell, cellLocale: Locale? = null) : Cell {
    override val locale = cellLocale ?: ExcelRow(cell.row).sheet.workbook.locale
    override val address: CellAddress
        get() = CellAddress(cell.rowIndex, cell.columnIndex)
    override val cellType: CellType
        get() {
            return when (if (cell.cellType == org.apache.poi.ss.usermodel.CellType.FORMULA)
                cell.cachedFormulaResultType
            else
                cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> CellType.DOUBLE
                org.apache.poi.ss.usermodel.CellType.STRING -> CellType.STRING
                org.apache.poi.ss.usermodel.CellType.BLANK -> CellType.BLANK
                org.apache.poi.ss.usermodel.CellType.BOOLEAN -> CellType.BOOL
                else ->
                    throw AppDataException(
                        suspectedLocation(this),
                        message("message.error.source.excel.unsupported_cell_type", cell.cellType)
                    )
            }
        }
    override val booleanValue: Boolean
        get() = cell.booleanCellValue
    override val longValue: Long
        get() = cell.numericCellValue.toLong()
    override val doubleValue: Double
        get() = cell.numericCellValue
    override val stringValue: String
        get() = cell.stringCellValue
    override val row: Row
        get() = ExcelRow(cell.row)

    /**
     * Set string cell value
     *
     * @param value String
     */
    override fun setCellValue(value: String) = cell.setCellValue(value)

    /**
     * Set boolean cell value
     *
     * @param value Boolean
     */
    override fun setCellValue(value: Boolean) = cell.setCellValue(value)

    /**
     * Set long cell value
     *
     * @param value Long
     */
    override fun setCellValue(value: Long) = cell.setCellValue(value.toDouble())

    /**
     * Set double cell value
     *
     * @param value Double
     */
    override fun setCellValue(value: Double) = cell.setCellValue(value)

    /**
     * Set cell [LocalDateTime] value
     *
     * @param value DateTime
     */
    override fun setCellValue(value: LocalDateTime) =
        cell.setCellValue(value.toDate())

    /**
     * Set cell [LocalDate] value
     *
     * @param value DateTime
     */
    override fun setCellValue(value: LocalDate) =
        cell.setCellValue(value.toDate())

    /**
     * Get excel cell value in string presentation
     *
     * @return String The string presentation of cell value
     */
    override fun asString(): String {
        val format = NumberFormat.getInstance().apply { isGroupingUsed = false }
        return when (if (cell.cellType == org.apache.poi.ss.usermodel.CellType.FORMULA) {
            cell.cachedFormulaResultType
        } else {
            cell.cellType
        }) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> format.format(cell.numericCellValue)
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.BLANK -> ""
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> throw AppDataException(
                suspectedLocation(this),
                message("message.error.source.excel.unsupported_cell_type", cell.cellType)
            )
        }
    }
}
