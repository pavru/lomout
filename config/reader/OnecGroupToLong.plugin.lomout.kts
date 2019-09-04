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

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.ReaderBuilder
import net.pototskiy.apps.lomout.api.callable.createReader
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.reader.LongAttributeReader
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.suspectedLocation
import java.text.ParseException

class OnecGroupToLong : LongAttributeReader(), ReaderBuilder {
    override operator fun invoke(attribute: Attribute, input: Cell, context: LomoutContext): Long? {
        try {
            return input.asString().drop(1).stringToLong(locale?.createLocale() ?: input.locale, false)
        } catch (e: ParseException) {
            throw AppDataException(suspectedLocation(attribute) + input, e.message, e)
        }
    }

    override fun build(): AttributeReader<out Any?> = createReader<OnecGroupToLong>()
}
