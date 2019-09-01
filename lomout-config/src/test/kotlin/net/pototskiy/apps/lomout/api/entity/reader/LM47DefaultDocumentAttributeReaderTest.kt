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

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class LM47DefaultDocumentAttributeReaderTest {
    internal class NestedType : Document() {
        var attr1: String = ""
        var attr2: String = ""

        companion object : DocumentMetadata(NestedType::class)
    }

    internal class TestType : Document() {
        var attr: NestedType = NestedType()

        companion object : DocumentMetadata(TestType::class)
    }

    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private var attr = TestType.attributes.getValue("attr")
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        xlsWorkbook = HSSFWorkbookFactory.createWorkbook()
        val xlsSheet = xlsWorkbook.createSheet("test-data")
        xlsSheet.isActive = true
        xlsTestDataCell = xlsSheet.createRow(0).createCell(0)
        workbook = ExcelWorkbook(xlsWorkbook)
        inputCell = workbook["test-data"][0]!![0]!!
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
    }

    @Test
    internal fun withEscapeTest() {
        val reader = DocumentAttributeReader().apply {
            delimiter = ','
            quotes = null
            valueDelimiter = '='
            valueQuote = null
            valueEscape = '\\'
        }
        xlsTestDataCell.setCellValue("attr1=value1\\,value1.1,attr2=value2")
        val list = reader.read(attr, inputCell)
        assertThat(list).isNotNull
        list as NestedType
        assertThat(list.attr1).isEqualTo("value1,value1.1")
        assertThat(list.attr2).isEqualTo("value2")
    }

    @Test
    internal fun withEscapeWrongTest() {
        val reader = DocumentAttributeReader().apply {
            delimiter = ','
            quotes = null
            valueDelimiter = '='
            valueQuote = null
            valueEscape = '\\'
        }
        xlsTestDataCell.setCellValue("attr1=value1,value1.1,attr2=value2")
        val list = reader.read(attr, inputCell)
        assertThat(list).isNotNull
        list as NestedType
        assertThat(list.attr1).isEqualTo("value1")
        assertThat(list.attr2).isEqualTo("value2")
    }

    @Test
    internal fun withQuoteNoEscapeTest() {
        val reader = DocumentAttributeReader().apply {
            delimiter = ','
            quotes = '\''
            valueDelimiter = '='
            valueQuote = '\''
            valueEscape = null
        }
        xlsTestDataCell.setCellValue("'attr1=value1,value1.1',attr2=value2")
        val list = reader.read(attr, inputCell)
        assertThat(list).isNotNull
        list as NestedType
        assertThat(list.attr1).isEqualTo("value1,value1.1")
        assertThat(list.attr2).isEqualTo("value2")
    }
}
