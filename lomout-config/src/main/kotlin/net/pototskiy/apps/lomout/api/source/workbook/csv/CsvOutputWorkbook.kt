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
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.util.*
import kotlin.contracts.contract

/**
 * CSV workbook for output to file.
 *
 * @property writer OutputStreamWriter
 * @property _printer CSVPrinter
 * @property printer CSVPrinter
 * @constructor
 */
class CsvOutputWorkbook(
    private val writer: OutputStreamWriter,
    csvFormat: CSVFormat,
    locale: Locale = DEFAULT_LOCALE
) : CsvWorkbook(locale) {

    /**
     * Constructor.
     *
     * @param source URL The CSV file URL
     * @param csvFormat CSVFormat The CSV format definition
     * @param workbookLocale Locale The CSV file locale
     * @constructor
     */
    constructor(
        source: URL,
        csvFormat: CSVFormat,
        workbookLocale: Locale = DEFAULT_LOCALE
    ) : this(FileOutputStream(source.file).writer(), csvFormat, workbookLocale) {
        this.sourceURL = source
    }

    private var _printer: CSVPrinter = csvFormat.print(writer)

    /**
     * Insert sheet to workbook.
     *
     * Only one sheet with name *default* can be inserted
     *
     * @param sheet The sheet name, must be *default*
     * @return The inserted sheet
     */
    override fun insertSheet(sheet: String): Sheet {
        if (sheet != CSV_SHEET_NAME) {
            throw AppDataException(suspectedLocation(this), message("message.error.source.csv.only_default_sheet"))
        }
        return CsvSheet(this).also { this.sheet = it }
    }

    /**
     * CSV printer.
     */
    val printer: CSVPrinter
        get() = _printer

    /**
     * Close workbook.
     */
    override fun close() {
        sheet?.writeLastRow()
        _printer.close()
        writer.close()
    }
}

/**
 * Test workbook is instance of [CsvOutputWorkbook]
 *
 * @param workbook CsvWorkbook
 * @throws AppDataException Wrong workbook type
 */
fun checkThatItIsCsvOutputWorkbook(workbook: CsvWorkbook) {
    contract {
        returns() implies (workbook is CsvOutputWorkbook)
    }
    if (workbook !is CsvOutputWorkbook) {
        throw AppDataException(suspectedLocation(workbook), message("message.error.source.csv.read_only"))
    }
}
