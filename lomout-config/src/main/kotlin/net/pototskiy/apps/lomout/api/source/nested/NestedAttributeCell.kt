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
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Attribute workbook cell
 *
 * @property address CellAddress
 * @property backingValue String
 * @property row NestedAttributeRow
 * @property cellType CellType
 * @property booleanValue Boolean
 * @property longValue Long
 * @property doubleValue Double
 * @property stringValue String
 * @constructor
 * @param address CellAddress The cell address
 * @param backingValue String The cell value
 * @param row NestedAttributeRow The cell row
 */
class NestedAttributeCell(
    override val address: CellAddress,
    private var backingValue: String,
    override val row: NestedAttributeRow
) : Cell {

    /**
     * Get cell value as string
     *
     * @return String
     */
    override fun asString(): String {
        return stringValue
    }

    /**
     * Cell value type, always is [CellType.STRING]
     */
    override val cellType: CellType = CellType.STRING
    /**
     * Cell boolean value
     */
    override val booleanValue: Boolean
        get() = throwNotSupportCellType()
    /**
     * Cell long value
     */
    override val longValue: Long
        get() = throwNotSupportCellType()
    /**
     * Cell double value
     */
    override val doubleValue: Double
        get() = throwNotSupportCellType()

    private fun throwNotSupportCellType(): Nothing {
        throw AppDataException(
            badPlace(this),
            message("message.error.source.nested.unsupported_type", NestedAttributeCell::class.simpleName)
        )
    }

    /**
     * Cell string value
     */
    override val stringValue: String
        get() = backingValue

    /**
     * Set cell string value
     *
     * @param value String
     */
    override fun setCellValue(value: String) {
        backingValue = value
    }

    /**
     * Set cell boolean value
     *
     * @param value Boolean
     * @return Nothing
     */
    override fun setCellValue(value: Boolean) = throwNotSupportCellType()

    /**
     * Set cell long value
     *
     * @param value Long
     * @return Nothing
     */
    override fun setCellValue(value: Long) = throwNotSupportCellType()

    /**
     * Set cell double value
     *
     * @param value Double
     * @return Nothing
     */
    override fun setCellValue(value: Double) = throwNotSupportCellType()

    /**
     * Set cell [LocalDateTime] value
     *
     * @param value The datetime value
     * @return Nothing
     */
    override fun setCellValue(value: LocalDateTime) = throwNotSupportCellType()

    /**
     * Set cell [LocalDate] value
     *
     * @param value The date value
     * @return Nothing
     */
    override fun setCellValue(value: LocalDate) = throwNotSupportCellType()
}
