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

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.reader
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.suspectedLocation
import kotlin.reflect.full.createInstance

/**
 * Default reader for [Document] attribute
 *
 * @property quote Char? The name-value pair quote, optional. This is parameter
 * @property delimiter Char The delimiter between pairs, default:','. This is parameter
 * @property valueQuote Char? The value quote, optional. This is parameter
 * @property valueDelimiter Char The delimiter between name and value, default:'='. This is parameter
 */
open class DocumentAttributeReader : AttributeReader<Document?>() {
    var quote: Char? = null
    var delimiter: Char = ','
    @Suppress("MemberVisibilityCanBePrivate")
    var escape: Char? = '\\'
    var valueQuote: Char? = '"'
    var valueDelimiter: Char = '='
    var valueEscape: Char? = '\\'

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): Document? {
        return when (input.cellType) {
            CellType.STRING -> {
                if (input.stringValue.isBlank()) return null
                val attrs = NestedAttributeWorkbook(
                    quote,
                    delimiter,
                    escape,
                    valueQuote,
                    valueDelimiter,
                    valueEscape,
                    attribute.name
                )
                attrs.string = input.stringValue
                val names = attrs[0][0]!!
                val values = attrs[0][1]!!
                val doc = try {
                    attribute.klass.createInstance() as Document
                } catch (e: IllegalArgumentException) {
                    throw AppConfigException(
                        suspectedLocation(attribute) + input, message("message.error.data.document.cannot_create")
                    )
                }
                val metaData = doc.documentMetadata
                names.forEachIndexed { c, cell ->
                    if (cell != null) {
                        val attrName = cell.stringValue
                        metaData.attributes[attrName]?.let {
                            doc.setAttribute(cell.stringValue, it.reader.read(it, values.getOrEmptyCell(c)))
                        }
                    }
                }
                doc
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                suspectedLocation(input) + attribute,
                message("message.error.data.document.cannot_read")
            )
        }
    }
}
