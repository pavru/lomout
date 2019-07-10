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

package net.pototskiy.apps.lomout.api.source.nested

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.QuoteMode

/**
 * Nested attribute list format
 *
 * @property quote Char? The name-value pair quote, null — no quote
 * @property delimiter Char The name-value pair delimiter
 * @property valueQuote Char? The value quote, null — no quote
 * @property valueDelimiter Char The name value delimiter
 * @constructor
 */
open class NestedAttributeListFormat(
    private val quote: Char?,
    private val delimiter: Char,
    private val valueQuote: Char?,
    private val valueDelimiter: Char
) {
    /**
     * CSV format for name-value pairs list
     *
     * @return CSVFormat
     */
    protected fun getAttrFormat(): CSVFormat {
        var format = CSVFormat.RFC4180
            .withRecordSeparator("")
            .withDelimiter(delimiter)
        if (quote != null) {
            format = format.withQuote(quote)
            format = format.withEscape('\\')
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }

    /**
     * Get CSV format for name value
     *
     * @return CSVFormat
     */
    protected fun getNameValueFormat(): CSVFormat {
        var format = CSVFormat.RFC4180
            .withRecordSeparator("")
            .withDelimiter(valueDelimiter)
        if (valueQuote != null) {
            format = format.withQuote(valueQuote)
            format = format.withEscape('\\')
        } else {
            format = format.withEscape('\\')
            format = format.withQuoteMode(QuoteMode.NONE)
        }
        return format
    }
}
