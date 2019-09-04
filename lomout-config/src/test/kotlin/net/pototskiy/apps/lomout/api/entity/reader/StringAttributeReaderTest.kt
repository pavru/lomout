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

import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.createCsvCell
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initStringValue
import net.pototskiy.apps.lomout.api.document.toAttribute
import net.pototskiy.apps.lomout.api.simpleTestContext
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class StringAttributeReaderTest {
    class TestType : Document() {
        var attr: String = initStringValue

        companion object : DocumentMetadata(TestType::class)
    }

    @BeforeEach
    internal fun setUp() {
        LomoutContext.setContext(simpleTestContext)
    }

    @Test
    internal fun readBoolTest() {
        var cell = createCsvCell("true")
        assertThat(cell.cellType).isEqualTo(CellType.BOOL)
        val reader = StringAttributeReader()
        assertThat(reader(TestType::attr.toAttribute(),cell)).isEqualTo("1")
        cell = createCsvCell("false")
        assertThat(reader(TestType::attr.toAttribute(),cell)).isEqualTo("0")
    }

    @Test
    internal fun readLongTest() {
        val cell = createCsvCell(3L.toString())
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        val reader = StringAttributeReader()
        assertThat(reader(TestType::attr.toAttribute(),cell)).isEqualTo("3")
        reader.locale = "en_EN"
        assertThat(reader(TestType::attr.toAttribute(),cell)).isEqualTo("3")
    }

    @Test
    internal fun readDoubleTest() {
        val cell = createCsvCell(3.3.toString())
        assertThat(cell.cellType).isEqualTo(CellType.DOUBLE)
        val reader = StringAttributeReader()
        assertThat(reader(TestType::attr.toAttribute(),cell)).isEqualTo("3.3")
        reader.locale = "en_EN"
        assertThat(reader(TestType::attr.toAttribute(),cell)).isEqualTo("3.3")
    }
}
