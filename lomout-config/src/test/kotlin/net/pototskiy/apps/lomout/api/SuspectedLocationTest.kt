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

package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.documentMetadata
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.setFileName
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class SuspectedLocationTest {

    class EntityType1 : Document() {
        var attr1: String? = null

        companion object : DocumentMetadata(EntityType1::class)
    }

    class EntityType2 : Document() {
        var attr2: String? = null

        companion object : DocumentMetadata(EntityType2::class)
    }

    private val attr1 = EntityType1::class.documentMetadata.attributes.getValue(EntityType1::attr1.name)
    private val attr2 = EntityType2::class.documentMetadata.attributes.getValue(EntityType2::attr2.name)

    @Test
    internal fun attributeInfo() {
        assertThat(suspectedLocation().attributeInfo()).isEqualTo("")
        val place = suspectedLocation(attr1)
        assertThat(place.attributeInfo()).isEqualTo("A:'attr1', E:'EntityType1'")
        val place2 = place + EntityType2::class
        assertThat(place2.attributeInfo()).isEqualTo("A:'attr1', E:'EntityType2'")
    }

    @Test
    fun cellInfo() {
        assertThat(suspectedLocation().cellInfo()).isEqualTo("")
        val excelWorkbook = HSSFWorkbook()
        excelWorkbook.setFileName(File("test.xls"))
        val workbook = ExcelWorkbook(excelWorkbook)
        val excelSheet = excelWorkbook.createSheet("sheet")
        val sheet = workbook["sheet"]
        val excelRow = excelSheet.createRow(3)
        val row = sheet[3]
        excelRow.createCell(5)
        val cell = row!![5]
        val place = suspectedLocation(cell!!)
        assertThat(place.cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row).cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row + sheet).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row + sheet + workbook).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat(suspectedLocation(workbook).cellInfo()).isEqualTo("W:'test.xls'")
        assertThat((suspectedLocation(workbook) + sheet).cellInfo()).isEqualTo("W:'test.xls', S:'sheet'")
        assertThat((suspectedLocation(workbook) + sheet + row).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4'")
        assertThat((suspectedLocation(workbook) + sheet + row + cell).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
    }

    @Test
    fun fieldInfo() {
        assertThat(suspectedLocation().fieldInfo()).isEqualTo("")
        val field = Field("field", 5, null)
        val place = suspectedLocation(field)
        assertThat(place.fieldInfo()).isEqualTo("F:'field(6(F))'")
    }

    @Test
    fun dataInfo() {
        assertThat(suspectedLocation().dataInfo()).isEqualTo("")
        assertThat(suspectedValue(56L).dataInfo()).isEqualTo("V:'56(Long)'")
    }

    @Test
    fun placeInfo() {
        assertThat(suspectedLocation().fieldInfo()).isEqualTo("")
        val place = suspectedValue(5.6) + attr2
        assertThat(place.placeInfo()).isEqualTo("Place: A:'attr2', E:'EntityType2', V:'5.6(Double)'.")
    }
}
