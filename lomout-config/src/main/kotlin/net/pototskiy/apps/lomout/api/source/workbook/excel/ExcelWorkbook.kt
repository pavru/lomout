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

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType
import java.util.*

/**
 * Excel workbook source file
 *
 * @param T Workbook implementation type
 * @property workbook POI workbook
 * @property forInput Read only workbook
 * @property name Workbook name
 * @property type Workbook type
 * @constructor
 */
class ExcelWorkbook<T : org.apache.poi.ss.usermodel.Workbook>(
    private val workbook: T,
    private val forInput: Boolean = true,
    override val locale: Locale = DEFAULT_LOCALE
) : Workbook {
    override val name: String
        get() = workbook.getFileName()
    override val type: WorkbookType
        get() = WorkbookType.EXCEL

    /**
     * Get workbook sheet by name
     *
     * @param sheet String The sheet name
     * @return ExcelSheet
     */
    override operator fun get(sheet: String): ExcelSheet =
        ExcelSheet(workbook.getSheet(sheet))

    /**
     * Get workbook sheet by the index
     *
     * @param sheet Int The sheet index, zero based
     * @return ExcelSheet Sheet
     */
    override operator fun get(sheet: Int): ExcelSheet =
        ExcelSheet(workbook.getSheetAt(sheet))

    /**
     * Check if workbook has a sheet
     *
     * @param sheet String The sheet name
     * @return Boolean true — sheet exists, false — no sheet in the workbook
     */
    override fun hasSheet(sheet: String): Boolean {
        return this.any { it.name == sheet }
    }

    /**
     * Get workbook sheet iterator
     *
     * @return Iterator<ExcelSheet>
     */
    override fun iterator(): Iterator<ExcelSheet> =
        ExcelSheetIterator(workbook)

    /**
     * Close workbook
     */
    override fun close() {
        if (!forInput) {
            workbook.getFile()?.outputStream()?.use {
                workbook.write(it)
            }
        }
        workbook.close()
    }

    /**
     * Insert a new sheet into the workbook
     *
     * @param sheet The sheet name
     * @return Sheet
     */
    override fun insertSheet(sheet: String): Sheet {
        return ExcelSheet(workbook.createSheet(sheet))
    }
}
