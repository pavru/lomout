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
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType
import java.io.File
import java.net.URL
import java.util.*

/**
 * CSV source file workbook
 *
 * @property workbookLocale Locale
 * @property sheet CsvSheet?
 * @property sourceURL URL
 * @property name String
 * @property type WorkbookType
 * @constructor
 */
abstract class CsvWorkbook(
    val workbookLocale: Locale = DEFAULT_LOCALE
) : Workbook {

    var sheet: CsvSheet? = null

    protected var sourceURL: URL = URL("file", "local", "virtual")

    /**
     * Workbook name (file name)
     */
    override val name: String
        get() = File(sourceURL.file).name // _fileName
    /**
     * Workbook type
     */
    override val type: WorkbookType
        get() = WorkbookType.CSV

    /**
     * Get sheet by name
     *
     * @param sheet String The sheet name
     * @return CsvSheet
     */
    override fun get(sheet: String): CsvSheet {
        if (sheet == CSV_SHEET_NAME) {
            return CsvSheet(this).also { this.sheet = it }
        } else {
            throw AppDataException(badPlace(this), message("message.error.source.csv.only_default_sheet"))
        }
    }

    /**
     * Get sheet by the index
     *
     * @param sheet Int The sheet index, zero based
     * @return CsvSheet
     */
    override fun get(sheet: Int): CsvSheet {
        if (sheet == 0) {
            return CsvSheet(this).also { this.sheet = it }
        } else {
            throw AppDataException(badPlace(this), message("message.error.source.csv.only_sheet_index_0"))
        }
    }

    /**
     * Test if workbook has a sheet with given name
     *
     * @param sheet String The sheet name to test
     * @return Boolean
     */
    override fun hasSheet(sheet: String): Boolean {
        return sheet == CSV_SHEET_NAME
    }

    /**
     * Get workbook sheet iterator
     *
     * @return Iterator<CsvSheet>
     */
    override fun iterator(): Iterator<CsvSheet> =
        CsvSheetIterator(this)
}
