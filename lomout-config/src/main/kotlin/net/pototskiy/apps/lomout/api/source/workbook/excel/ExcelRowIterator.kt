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

import org.apache.poi.ss.usermodel.Row

/**
 * Excel sheet row iterator
 *
 * @property iterator MutableIterator<(org.apache.poi.ss.usermodel.Row..org.apache.poi.ss.usermodel.Row?)>
 * @constructor
 */
class ExcelRowIterator(sheet: org.apache.poi.ss.usermodel.Sheet) : Iterator<ExcelRow> {
    private val iterator = sheet.iterator()

    /**
     * Check if row has next cell
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = iterator.hasNext()

    /**
     * Get next sheet row
     *
     * @return ExcelRow
     */
    override fun next(): ExcelRow =
        ExcelRow(iterator.next() as Row)
}
