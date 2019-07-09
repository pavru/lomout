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

package net.pototskiy.apps.lomout.api.source.workbook.excel

import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

@Suppress("MagicNumber")
internal class XlsmWorkbookTest {

    @Test
    internal fun workbookBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/test-xlsm.xlsm"
            ).toURI().toURL()
        ).use { workbook ->
            assertThat(workbook).isInstanceOf(ExcelWorkbook::class.java)
            assertThat(workbook.type).isEqualTo(WorkbookType.EXCEL)
            assertThat(workbook.name).contains("test-xlsm.xlsm")
            assertThat(workbook.hasSheet("Sheet1")).isEqualTo(true)
            assertThat(workbook.hasSheet("Sheet2")).isEqualTo(true)
            assertThat(workbook.hasSheet("Sheet5")).isEqualTo(false)
            assertThat(workbook["Sheet1"]).isInstanceOf(ExcelSheet::class.java)
            assertThat(workbook["Sheet1"].name).isEqualTo(workbook[0].name)
        }
    }

    @Test
    internal fun sheetBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/test-xlsm.xlsm"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet1 = workbook["Sheet1"]
            val sheet3 = workbook["Sheet2"]
            assertThat(sheet1.name).isEqualTo("Sheet1")
            assertThat(sheet1.workbook.name).isEqualTo(workbook.name)
            assertThat(sheet1[0]).isNotNull.isInstanceOf(ExcelRow::class.java)
            assertThat(sheet3[0]).isNull()
        }
    }

    @Test
    internal fun rowBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/test-xlsm.xlsm"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet = workbook["Sheet1"]
            val row = sheet[0]
            assertThat(row).isNotNull.isInstanceOf(ExcelRow::class.java)
            assertThat(row?.sheet?.name).isEqualTo(sheet.name)
            assertThat(row?.countCell()).isEqualTo(4)
            assertThat(row!![0]).isNotNull.isInstanceOf(ExcelCell::class.java)
            assertThat(row[5]).isNull()
            assertThat(row.getOrEmptyCell(5)).isNotNull.isInstanceOf(ExcelCell::class.java)
        }
    }

    @Test
    internal fun cellBasicTest() {
        WorkbookFactory.create(
            File(
                "${System.getenv("TEST_DATA_DIR")}/test-xlsm.xlsm"
            ).toURI().toURL()
        ).use { workbook ->
            val sheet = workbook["Sheet1"]
            var row = sheet[0]!!
            assertThat(row[0]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[0]!!.doubleValue).isEqualTo(1.0)
            assertThat(row[0]!!.longValue).isEqualTo(1L)
            assertThat(row[1]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[1]!!.doubleValue).isEqualTo(2.0)
            assertThat(row[1]!!.longValue).isEqualTo(2L)
            assertThat(row[2]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[2]!!.doubleValue).isEqualTo(3.0)
            assertThat(row[2]!!.longValue).isEqualTo(3L)
            assertThat(row[3]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[3]!!.doubleValue).isEqualTo(5.0)
            assertThat(row[3]!!.longValue).isEqualTo(5L)
            row = sheet[2]!!
            assertThat(row[0]!!.cellType).isEqualTo(CellType.STRING)
            assertThat(row[0]!!.stringValue).isEqualTo("Column1")
            assertThat(row[1]!!.cellType).isEqualTo(CellType.STRING)
            assertThat(row[1]!!.stringValue).isEqualTo("Column2")
            assertThat(row[2]!!.cellType).isEqualTo(CellType.STRING)
            assertThat(row[2]!!.stringValue).isEqualTo("Column3")
            assertThat(row[3]!!.cellType).isEqualTo(CellType.STRING)
            assertThat(row[3]!!.stringValue).isEqualTo("Column4")
            row = sheet[3]!!
            assertThat(row[0]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[0]!!.doubleValue).isEqualTo(11.0)
            assertThat(row[0]!!.longValue).isEqualTo(11L)
            assertThat(row[1]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[1]!!.doubleValue).isEqualTo(12.0)
            assertThat(row[1]!!.longValue).isEqualTo(12L)
            assertThat(row[2]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[2]!!.doubleValue).isEqualTo(13.0)
            assertThat(row[2]!!.longValue).isEqualTo(13L)
            assertThat(row[3]!!.cellType).isEqualTo(CellType.DOUBLE)
            assertThat(row[3]!!.doubleValue).isEqualTo(25.0)
            assertThat(row[3]!!.longValue).isEqualTo(25L)
            Unit
        }
    }
}
