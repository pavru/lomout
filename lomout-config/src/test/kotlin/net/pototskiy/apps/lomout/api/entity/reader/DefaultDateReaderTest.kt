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
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType
import net.pototskiy.apps.lomout.api.entity.values.millis
import net.pototskiy.apps.lomout.api.entity.values.toDate
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvCell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.commons.csv.CSVFormat
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Execution(ExecutionMode.CONCURRENT)
internal class DefaultDateReaderTest {
    internal class TestType : Document() {
        var attr: LocalDate = LocalDate.MIN

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
    internal fun readDoubleCellTest() {
        val expected = LocalDate.parse("15.03.31", DateTimeFormatter.ofPattern("d.M.yy"))
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        val readerPattern = DateAttributeReader().apply { pattern = "d.M.uu" }
        xlsTestDataCell.setCellValue(HSSFDateUtil.getExcelDate(expected.toDate()))
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, inputCell)).isEqualTo(expected)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, inputCell)).isEqualTo(expected)
        assertThat(readerPattern.read(attr, inputCell)).isEqualTo(expected)
    }

    @Test
    internal fun readLongCellTest() {
        val expected = LocalDate.parse("15.03.31", DateTimeFormatter.ofPattern("d.M.uu"))
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        val readerPattern = DateAttributeReader().apply { pattern = "d.M.uu" }
        val cell = createCsvCell(expected.millis.toString())
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(readerEnUs.read(attr, cell)).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, cell)).isEqualTo(expected)
        assertThat(cell.cellType).isEqualTo(CellType.LONG)
        assertThat(readerPattern.read(attr, cell)).isEqualTo(expected)
    }

    @Test
    internal fun readBooleanOrBlankCellTest() {
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        val readerWithPattern = DateAttributeReader().apply { pattern = "d.M.uu" }
        xlsTestDataCell.setCellValue(true)
        assertThat(inputCell.cellType).isEqualTo(CellType.BOOL)
        assertThat(readerEnUs.read(attr, inputCell)).isNull()
        assertThat(readerRuRu.read(attr, inputCell)).isNull()
        assertThat(readerWithPattern.read(attr, inputCell)).isNull()
        xlsTestDataCell.setBlank()
        assertThat(readerEnUs.read(attr, inputCell)).isNull()
        assertThat(readerRuRu.read(attr, inputCell)).isNull()
        assertThat(readerWithPattern.read(attr, inputCell)).isNull()
    }

    @Test
    internal fun readStringCellTest() {
        val expected = LocalDate.now()
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        xlsTestDataCell.setCellValue(
            expected.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale("en_US".createLocale()))
        )
        val dateString = expected.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale("en_US".createLocale())
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String '$dateString' cannot be converted to date with the locale 'ru_RU'.")
        val expectedText = expected.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale("ru_RU".createLocale())
        )
        xlsTestDataCell.setCellValue(
            expected.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale("ru_RU".createLocale()))
        )
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String '$expectedText' cannot be converted to date with the locale 'en_US'.")
        assertThat(readerRuRu.read(attr, inputCell)).isEqualTo(expected)
    }

    @Test
    internal fun readStringCellWithPatternTest() {
        val expected = LocalDate.now()
        val readerEnUs = DateAttributeReader().apply { pattern = "M/d/yy" }
        val readerRuRu = DateAttributeReader().apply { pattern = "d.M.yy" }
        xlsTestDataCell.setCellValue(expected.format(DateTimeFormatter.ofPattern("M/d/yy")))
        val dateString = expected.format(DateTimeFormatter.ofPattern("M/d/yy"))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, inputCell) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("String '$dateString' cannot be converted to date with the pattern 'd.M.yy'.")
        xlsTestDataCell.setCellValue(expected.format(DateTimeFormatter.ofPattern("d.M.yy")))
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
        assertThat(readerRuRu.read(attr, inputCell)).isEqualTo(expected)
    }

    @Test
    internal fun defaultDateReaderTest() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[SupportAttributeType.dateType]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReader::class.java)
        reader as DateAttributeReader
        assertThat(reader.locale).isNull()
        assertThat(reader.pattern).isEqualTo("d.M.uu")
    }

    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
