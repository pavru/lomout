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

package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.values.CSVValueFormat
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType

/**
 * Default reader for **List&lt;String&gt;** attribute
 *
 * @property quotes Char? The value quote, optional. This is parameter
 * @property delimiter Char The list delimiter: default:','. This is parameter
 * @property escape The escape char like in CSV format. This is parameter.
 */
open class StringListAttributeReader : AttributeReader<List<String>?>() {
    var quotes: Char? = null
    var delimiter: Char = ','
    var escape: Char? = null

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): List<String>? {
        return when (input.cellType) {
            CellType.BLANK -> null
            else -> {
                input.asString().reader().use { reader ->
                    CSVValueFormat(delimiter, quotes, escape).format
                        .parse(reader)
                        .records
                        .map { it.toList() }.flatten()
                        .map { it }
                }
            }
        }
    }
}
