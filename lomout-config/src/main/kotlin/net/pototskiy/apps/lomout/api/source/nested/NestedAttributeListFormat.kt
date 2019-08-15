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

import net.pototskiy.apps.lomout.api.entity.values.CSVValueFormat
import org.apache.commons.csv.CSVFormat

/**
 * Nested attribute list format
 *
 * @param delimiter The attribute csv delimiter
 * @param quotes The attribute csv quotes, null - no quotes
 * @param escape The attribute csv escape symbol, null - double quotes are used
 * @property valueCSVFormat The name-value pair csv format
 * @constructor
 */
open class NestedAttributeListFormat(
    delimiter: Char,
    quotes: Char?,
    escape: Char?,
    private val valueCSVFormat: CSVValueFormat
) : CSVValueFormat(delimiter, quotes, escape) {
    /**
     * Secondary constructor with full csv specification
     *
     * @param delimiter The attribute csv delimiter
     * @param quotes The attribute csv quotes, null - no quotes
     * @param escape The attribute csv escape symbol, null - double quotes are used
     * @param valueDelimiter The name-value csv delimiter
     * @param valueQuotes The attribute value csv quotes, null - no quotes
     * @param valueEscape The attribute value csv escape symbol, null - double quotes are used
     */
    constructor(
        delimiter: Char,
        quotes: Char?,
        escape: Char?,
        valueDelimiter: Char,
        valueQuotes: Char?,
        valueEscape: Char?
    ) : this(delimiter, quotes, escape, CSVValueFormat(valueDelimiter, valueQuotes, valueEscape))

    /**
     * CSV format for name-value pairs list
     *
     * @return CSVFormat
     */
    protected fun getAttrFormat(): CSVFormat = format

    /**
     * Get CSV format for name value
     *
     * @return CSVFormat
     */
    protected fun getNameValueFormat(): CSVFormat = valueCSVFormat.format
}
