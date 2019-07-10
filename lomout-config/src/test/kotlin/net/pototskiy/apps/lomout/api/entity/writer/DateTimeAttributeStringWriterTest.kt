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

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType
import net.pototskiy.apps.lomout.api.entity.writer
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.plugable.Writer
import net.pototskiy.apps.lomout.api.plugable.WriterBuilder
import net.pototskiy.apps.lomout.api.plugable.createWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DateTimeAttributeStringWriterTest {
    internal class TestType : Document() {
        var attr1: LocalDateTime = LocalDateTime.MIN
        @Writer(Attr2Writer::class)
        var attr2: LocalDateTime = LocalDateTime.MIN

        companion object : DocumentMetadata(TestType::class)

        class Attr2Writer : WriterBuilder {
            override fun build(): AttributeWriter<out Any?> = createWriter<DateTimeAttributeStringWriter> {
                pattern = null
            }
        }
    }

    private lateinit var file: File
    private lateinit var workbook: Workbook
    private lateinit var cell: Cell
    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    internal fun setUp() {
        @Suppress("GraziInspection")
        file = tempDir.resolve("attributes.xls").toFile()
        workbook = WorkbookFactory.create(file.toURI().toURL(), DEFAULT_LOCALE, false)
        cell = workbook.insertSheet("test").insertRow(0).insertCell(0)
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
        file.delete()
    }

    @Test
    internal fun simpleWriteTest() {
        val attr = TestType.attributes.getValue("attr1")
        val now = LocalDateTime.now()
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LocalDateTime>).write(now, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue)
            .isEqualTo(now.format(DateTimeFormatter.ofPattern("d.M.uu H:m")))
    }

    @Test
    internal fun simpleWriteLocaleTest() {
        val attr = TestType.attributes.getValue("attr2")
        val now = LocalDateTime.now()
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LocalDateTime>).write(now, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue)
            .isEqualTo(
                now.format(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(DEFAULT_LOCALE)
                )
            )
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = TestType.attributes.getValue("attr1")
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LocalDateTime?>).write(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[SupportAttributeType.dateTimeType]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(DateTimeAttributeStringWriter::class.java)
        writer as DateTimeAttributeStringWriter
        assertThat(writer.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(writer.pattern).isEqualTo("d.M.uu H:m")
    }
}
