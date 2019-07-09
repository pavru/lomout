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

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.badData
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.unknownPlace
import org.apache.commons.csv.CSVRecord
import java.io.StringReader

/**
 * Nested attributes list parser
 *
 * @constructor
 * @param quote Char? The name-value pair quote, null — no quote
 * @param delimiter Char The name-value pair delimiter
 * @param valueQuote Char? The value quote, null — no quote
 * @param valueDelimiter Char The delimiter between name and value
 */
class NestedAttributeListParser(
    quote: Char?,
    delimiter: Char,
    valueQuote: Char?,
    valueDelimiter: Char
) : NestedAttributeListFormat(quote, delimiter, valueQuote, valueDelimiter) {

    /**
     * Parse list string to map of name→value
     *
     * @param string String The list as string
     * @return Map<String, String> The map of name→value
     */
    fun parse(string: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            string.reader().use { attrReader ->
                parsePairsList(attrReader, result)
            }
        } catch (e: AppDataException) {
            throw AppDataException(
                badData(string) + this,
                message("message.error.source.nested.cannot_parse_string_to_list", string)
            )
        }
        return result
    }

    private fun parsePairsList(attrReader: StringReader, result: MutableMap<String, String>) {
        return getAttrFormat().parse(attrReader).use { attrParser ->
            attrParser.records.firstOrNull()?.forEach { attr ->
                attr?.reader()?.use { valueReader ->
                    parseNameValue(valueReader, result)
                }
            }
        }
    }

    private fun parseNameValue(valueReader: StringReader, result: MutableMap<String, String>) {
        return getNameValueFormat().parse(valueReader).use { valueParser ->
            valueParser.records.firstOrNull()?.let {
                addToResultMap(result, it)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun addToResultMap(result: MutableMap<String, String>, it: CSVRecord) {
        try {
            result[it[0]] = try {
                it[1]
            } catch (e: Exception) {
                ""
            }
        } catch (e: Exception) {
            throw AppDataException(unknownPlace(), message("message.error.source.nested.cannot_parse"))
        }
    }
}
