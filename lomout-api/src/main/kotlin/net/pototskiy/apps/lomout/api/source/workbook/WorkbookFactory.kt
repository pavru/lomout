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

import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvOutputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.setFileName
import org.apache.commons.csv.CSVFormat
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.net.URL
import java.util.*

/**
 * Source data workbook factory
 *
 * Create workbook from source file with respect to file extension
 */
class WorkbookFactory {
    /**
     * Companion object
     */
    companion object {
        /**
         * Create CSV or Excel workbook from the file
         *
         * @param source The source file URL
         * @param workbookLocale The source file locale
         * @param forInput The flag that workbook will be used for reading
         * @return The created workbook
         */
        fun create(source: URL, workbookLocale: Locale = DEFAULT_LOCALE, forInput: Boolean = true): Workbook {
            val fileName = File(source.file).name
            val file = File(fileName)
            return when (file.extension.toLowerCase()) {
                "xls" -> {
                    val wb = hssfWorkbook(forInput, source)
                    wb.setFileName(File(source.file))
                    ExcelWorkbook(wb, forInput)
                }
                "xlsx", "xlsm" -> {
                    val wb = xssfWorkbook(forInput, source)
                    wb.setFileName(File(source.file))
                    ExcelWorkbook(wb, forInput)
                }
                "csv" -> {
                    val format = CSVFormat.RFC4180.withEscape('\\')
                    if (forInput)
                        CsvInputWorkbook(source, format, workbookLocale)
                    else
                        CsvOutputWorkbook(source, format, workbookLocale)
                }
                else ->
                    throw AppException("Unsupported source file format, file '$fileName'.")
            }
        }

        private fun xssfWorkbook(forInput: Boolean, input: URL): org.apache.poi.ss.usermodel.Workbook =
            if (forInput) {
                XSSFWorkbook(input.openStream())
            } else {
                XSSFWorkbook()
            }

        private fun hssfWorkbook(
            forInput: Boolean,
            input: URL
        ): HSSFWorkbook = if (forInput) {
            HSSFWorkbook(input.openStream()).also {
                HSSFFormulaEvaluator.evaluateAllFormulaCells(it)
            }
        } else {
            HSSFWorkbook()
        }
    }
}
