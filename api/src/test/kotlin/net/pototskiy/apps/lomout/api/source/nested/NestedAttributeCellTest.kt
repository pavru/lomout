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

package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class NestedAttributeCellTest {
    @Test
    internal fun getCellAddressTest() {
        val workbook = NestedAttributeWorkbook(null, ',', null, '=', "test")
        workbook.string = "attr1=value1,attr2=value2"
        val cell = workbook[0][1]!![1]
        assertThat(cell).isNotNull
        assertThat(cell?.address).isEqualTo(CellAddress(1, 1))
        assertThat(cell?.row?.rowNum).isEqualTo(1)
    }

    @Test
    internal fun notAllowOperationsTest() {
        val workbook = NestedAttributeWorkbook(null, ',', null, '=', "test")
        workbook.string = "attr1=value1,attr2=value2"
        val cell = workbook[0][1]!![1]
        assertThatThrownBy { cell?.booleanValue }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.longValue }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.doubleValue }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.doubleValue }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(true) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(1L) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(1.1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(LocalDateTime.now()) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(LocalDate.now()) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
    }
}
