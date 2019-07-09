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

/**
 * Workbook row interface
 *
 * @property sheet Sheet
 * @property rowNum Int
 */
interface Row : Iterable<Cell?> {
    /**
     * Row sheet
     */
    val sheet: Sheet
    /**
     * Row number(index), zero based
     */
    val rowNum: Int

    /**
     * Row cells count
     *
     * @return Int
     */
    fun countCell(): Int

    /**
     * Get cell by the index
     *
     * @param column Int The cell index(column), zero base
     * @return Cell?
     */
    operator fun get(column: Int): Cell?

    /**
     * Insert cell in row by the index
     *
     * @param column Int The cell index(column), zero based
     * @return Cell The inserted cell
     */
    fun insertCell(column: Int): Cell

    /**
     * Get cell by index
     * Return cell from row or empty cell if it does not exist
     *
     * @param column Int The cell index(column), zero based
     * @return Cell
     */
    fun getOrEmptyCell(column: Int): Cell
}
