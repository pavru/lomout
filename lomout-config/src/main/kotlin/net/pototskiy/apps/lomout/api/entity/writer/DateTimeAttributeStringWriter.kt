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

package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.callable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import java.time.LocalDateTime

/**
 * Default writer for [LocalDateTime] attribute
 *
 * @property locale String The value locale, default: system locale. This is parameter
 * @property pattern String? The datetime locale,optional(use locale). This is parameter
 */
open class DateTimeAttributeStringWriter : AttributeWriter<LocalDateTime?>() {
    var locale: String? = null
    var pattern: String? = null

    override fun write(value: LocalDateTime?, cell: Cell) {
        value?.let { dateValue ->
            cell.setCellValue(
                pattern?.let {
                    dateValue.datetimeToString(it)
                } ?: dateValue.datetimeToString(locale?.createLocale() ?: cell.locale)
            )
        }
    }
}
