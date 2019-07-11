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
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.suspectedLocation
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import kotlin.contracts.contract

/**
 * CSV workbook to read data from a file.
 *
 * @property reader InputStreamReader
 * @property _parser CSVParser
 * @property parser CSVParser
 * @constructor
 */
class CsvInputWorkbook(
    private val reader: InputStreamReader,
    csvFormat: CSVFormat,
    workbookLocale: Locale = DEFAULT_LOCALE
) : CsvWorkbook(workbookLocale) {

    /**
     * Constructor
     *
     * @param source URL
     * @param csvFormat CSVFormat
     * @param workbookLocale Locale
     * @constructor
     */
    constructor(
        source: URL,
        csvFormat: CSVFormat,
        workbookLocale: Locale = DEFAULT_LOCALE
    ) : this(source.openStream().reader(), csvFormat, workbookLocale) {
        this.sourceURL = source
    }

    private var _parser: CSVParser = csvFormat.parse(reader)

    /**
     * Insert sheet to work book, not supported
     *
     * @param sheet String
     * @return Sheet
     */
    override fun insertSheet(sheet: String): Sheet {
        throw AppDataException(suspectedLocation(this), message("message.error.source.csv.sheet_insert_not_allowed", this.name))
    }

    /**
     * CSV parser
     */
    val parser: CSVParser
        get() = _parser

    /**
     * Close workbook
     */
    override fun close() {
        _parser.close()
        reader.close()
    }
}

/**
 * Test if workbook is instance of [CsvInputWorkbook]
 *
 * @param workbook CsvWorkbook
 * @throws AppDataException Wrong workbook type
 */
fun checkThatItIsCsvInputWorkbook(workbook: CsvWorkbook) {
    contract {
        returns() implies (workbook is CsvInputWorkbook)
    }
    if (workbook !is CsvInputWorkbook) {
        throw AppDataException(suspectedLocation(workbook), message("message.error.source.csv.read_only", workbook.name))
    }
}
