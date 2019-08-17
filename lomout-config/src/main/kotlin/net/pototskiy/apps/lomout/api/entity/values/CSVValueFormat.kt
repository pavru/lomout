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

package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.QuoteMode

/**
 * Csv string list format
 *
 * @property delimiter The csv delimiter
 * @property quotes The csv quotes, null - no quotes
 * @property escape The csv escpae symbol, null - qoubled quotes
 */
open class CSVValueFormat(
    val delimiter: Char = ',',
    @Suppress("MemberVisibilityCanBePrivate") val quotes: Char? = null,
    @Suppress("MemberVisibilityCanBePrivate") val escape: Char? = null
) {
    /**
     * Csv format
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val format: CSVFormat by lazy {
        if (quotes == null && escape == null) {
            throw AppDataException(
                suspectedLocation(),
                message("message.error.data.csv.wrong_quotes_escape")
            )
        }
        var format = CSVFormat.RFC4180
            .withRecordSeparator("")
            .withDelimiter(delimiter)
        format = escape?.let { format.withEscape(it) } ?: format.withEscape(null)
        format = quotes?.let { format.withQuote(it) } ?: format.withQuoteMode(QuoteMode.NONE)
        format
    }
}
