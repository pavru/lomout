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

import java.io.StringWriter

/**
 * Nested attribute printer. Print map of attributes into string
 *
 * @constructor
 * @param quote Char? The name-value pair quote, null — no quote
 * @param delimiter Char The delimiter between name-value pairs
 * @param valueQuote Char? The value quote, null — no quote
 * @param valueDelimiter Char The delimiter between name and value
 */
class NestedAttributeListPrinter(
    quote: Char?,
    delimiter: Char,
    escape: Char?,
    valueQuote: Char?,
    valueDelimiter: Char,
    valueEscape: Char?
) : NestedAttributeListFormat(quote, delimiter, escape, valueQuote, valueDelimiter, valueEscape) {

    /**
     * Convert map of attributes(name→value) to string
     *
     * @param value Map<String, String> The attributes map
     * @return String
     */
    fun print(value: Map<String, String>): String {
        val values = value.map { (attr, value) ->
            StringWriter().use { writer ->
                getNameValueFormat().print(writer).use { it.printRecord(attr, value) }
                writer.toString()
            }
        }
        return StringWriter().use { writer ->
            getAttrFormat().print(writer).use { printer ->
                printer.printRecord(values)
            }
            writer.toString()
        }
    }
}
