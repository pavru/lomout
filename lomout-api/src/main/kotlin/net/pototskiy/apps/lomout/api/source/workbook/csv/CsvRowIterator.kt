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

/**
 * CSV sheet row iterator
 *
 * @property sheet CsvSheet
 * @property iterator MutableIterator<(org.apache.commons.csv.CSVRecord..org.apache.commons.csv.CSVRecord?)>
 * @property index Int
 * @constructor
 */
class CsvRowIterator(private val sheet: CsvSheet) : Iterator<CsvRow> {
    init {
        checkThatItIsCsvInputWorkbook(sheet.workbook as CsvWorkbook)
    }

    private val iterator = sheet.parser.iterator()
    private var index = 0

    /**
     * Test if sheet has a next row
     *
     * @return Boolean
     */
    override fun hasNext(): Boolean = iterator.hasNext()

    /**
     * Get sheet next row
     *
     * @return CsvRow
     */
    override fun next(): CsvRow {
        val v = iterator.next()
        return CsvRow(index, v, sheet).apply { index++ }
    }
}
