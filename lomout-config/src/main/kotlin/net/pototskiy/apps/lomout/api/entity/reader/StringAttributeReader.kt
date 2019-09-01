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

import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell

/**
 * Default reader for [String] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 * @property numberGroupingUsed The flag of digits grouping. This is parameter.
 */
open class StringAttributeReader : AttributeReader<String?>() {
    var locale: String? = null
    @Suppress("MemberVisibilityCanBePrivate")
    var numberGroupingUsed: Boolean = false

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): String? =
        input.readString(locale?.createLocale(), numberGroupingUsed)?.let { it }
}
