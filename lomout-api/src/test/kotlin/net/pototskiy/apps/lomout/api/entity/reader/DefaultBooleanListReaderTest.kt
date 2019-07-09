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
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
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
import java.text.ParseException

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultBooleanListReaderTest {

    internal class TestType : Document() {
        var attr: List<Boolean> = emptyList()

        companion object : DocumentMetadata(TestType::class)
    }

    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private var attr = TestType.attributes.getValue("attr")
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        @Suppress("UNCHECKED_CAST")
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
        val reader = BooleanListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(1.0)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
    }

    @Test
    internal fun readStringEnUsCorrectCellTest() {
        val readerEnUs = BooleanListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("true,false, false")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell))
            .hasSize(3)
            .containsExactlyElementsOf(listOf(true, false, false))
    }

    @Test
    internal fun readStringEnUsIncorrectCellTest() {
        val readerEnUs = BooleanListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("string, string, string")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(ParseException::class.java)
    }

    @Test
    internal fun readStringRuRuCorrectCellTest() {
        val readerRuRU = BooleanListAttributeReader().apply { locale = "ru_RU" }
        @Suppress("GraziInspection")
        xlsTestDataCell.setCellValue("иСтина,Ложь, ложь")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerRuRU.read(attr, inputCell))
            .hasSize(3)
            .containsExactlyElementsOf(listOf(true, false, false))
    }

    @Test
    internal fun defaultBooleanListReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[SupportAttributeType.booleanListType] as? AttributeReader<List<Boolean>>
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReader::class.java)
        reader as AttributeReader<List<Boolean>>
    }
}
