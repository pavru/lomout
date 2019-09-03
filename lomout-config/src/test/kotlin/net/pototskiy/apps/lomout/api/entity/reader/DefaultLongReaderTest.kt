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

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createCsvCell
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultLongReaderTest {
    internal class TestType : Document() {
        var attr: Long = 0L

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
    internal fun readBlankCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setBlank()
        assertThat(reader.read(attr, inputCell)).isNull()
    }

    @Test
    internal fun readDoubleCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(2.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, inputCell)).isEqualTo(2)
        xlsTestDataCell.setCellValue(2.2)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(TypeCastException::class.java)
    }

    @Test
    internal fun readLongCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        val cell = createCsvCell("11")
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(reader.read(attr, cell)).isEqualTo(11)
    }

    @Test
    internal fun readBooleanCellTest() {
        val reader = LongAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(true)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)).isEqualTo(1)
        xlsTestDataCell.setCellValue(false)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(reader.read(attr, inputCell)).isEqualTo(0)
    }

    @Test
    internal fun readStringEnUsCellTest() {
        val readerEnUs = LongAttributeReader().apply { locale = "en_US" }
        val readerRuRu = LongAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue("11")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(11L)
        assertThat(readerRuRu.read(attr, inputCell)).isEqualTo(11L)
        @Suppress("GraziInspection", "SpellCheckingInspection")
        xlsTestDataCell.setCellValue("xxxx")
        assertThatThrownBy {
            readerEnUs.read(attr, inputCell)
        }.isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String cannot be parsed to long.")
    }

    @Test
    internal fun readWithWorkbookLocaleTest() {
        xlsTestDataCell.setCellValue("3")
        val reader = LongAttributeReader().apply {
            locale = null
        }
        assertThat(reader.read(attr, inputCell)).isEqualTo(3L)
    }

    @Test
    internal fun defaultLongReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[SupportAttributeType.longType]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReader::class.java)
        reader as LongAttributeReader
        assertThat(reader.locale).isEqualTo(DEFAULT_LOCALE_STR)
    }
}
