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

package net.pototskiy.apps.lomout.api.source.workbook

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Workbook cell interface
 *
 * @property address The cell address
 * @property cellType The cell type
 * @property booleanValue The cell value as Long
 * @property longValue The cell value as long
 * @property doubleValue The cell value ad double
 * @property stringValue The cell value as string
 * @property value The cell value
 * @property row The cell row
 */
interface Cell {
    /**
     * Cell address (row,column)
     */
    val address: CellAddress
    /**
     * Cell value type
     */
    val cellType: CellType
    /**
     * Cell boolean value
     */
    val booleanValue: Boolean
    /**
     * Cell long value
     */
    val longValue: Long
    /**
     * Cell double value
     */
    val doubleValue: Double
    /**
     * Cell string value
     */
    val stringValue: String
    /**
     * Cell value
     */
    val value: Any?
        get() {
            return when (cellType) {
                CellType.LONG -> longValue
                CellType.DOUBLE -> doubleValue
                CellType.BOOL -> booleanValue
                CellType.STRING -> stringValue
                CellType.BLANK -> null
            }
        }

    /**
     * Set string cell value
     *
     * @param value String
     */
    fun setCellValue(value: String)

    /**
     * Set cell boolean value
     *
     * @param value Boolean
     */
    fun setCellValue(value: Boolean)

    /**
     * Set cell long value
     * @param value Long
     */
    fun setCellValue(value: Long)

    /**
     * Set cell double value
     *
     * @param value Double
     */
    fun setCellValue(value: Double)

    /**
     * Set cell [LocalDateTime] value
     *
     * @param value DateTime
     */
    fun setCellValue(value: LocalDateTime)

    /**
     * Set cell [LocalDate] value
     *
     * @param value DateTime
     */
    fun setCellValue(value: LocalDate)

    /**
     * Cell row
     */
    val row: Row

    /**
     * Cell value as string
     *
     * @return String
     */
    fun asString(): String
}
