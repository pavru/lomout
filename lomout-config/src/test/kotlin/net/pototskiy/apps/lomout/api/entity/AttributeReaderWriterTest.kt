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

package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.callable.AttributeWriter
import net.pototskiy.apps.lomout.api.callable.Reader
import net.pototskiy.apps.lomout.api.callable.ReaderBuilder
import net.pototskiy.apps.lomout.api.callable.Writer
import net.pototskiy.apps.lomout.api.callable.WriterBuilder
import net.pototskiy.apps.lomout.api.callable.createReader
import net.pototskiy.apps.lomout.api.callable.createWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvCell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import org.apache.commons.csv.CSVFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@Suppress("MagicNumber")
internal class AttributeReaderWriterTest {

    class TestReader : AttributeReader<Long?>() {
        override fun read(attribute: DocumentMetadata.Attribute, input: Cell): Long? {
            return input.asString().toLong()
        }
    }

    companion object {
        var testVal = 0L
    }

    class TestWriter : AttributeWriter<Long?>() {
        override fun write(value: Long?, cell: Cell) {
            testVal = value!!
        }
    }

    class EntityType : Document() {
        @Reader(TestReaderBuilder::class)
        @Writer(TestWriterBuilder::class)
        var test: Long = 0L

        companion object : DocumentMetadata(EntityType::class)

        class TestReaderBuilder : ReaderBuilder {
            override fun build(): AttributeReader<out Any?> {
                return createReader<TestReader>()
            }
        }

        class TestWriterBuilder : WriterBuilder {
            override fun build(): AttributeWriter<out Any?> {
                return createWriter<TestWriter>()
            }
        }
    }

    @Test
    internal fun attributeWithReaderPluginTest() {
        val attr = EntityType.attributes.getValue("test")
        assertThat(attr.reader).isNotNull.isInstanceOf(AttributeReader::class.java)
        @Suppress("UNCHECKED_CAST")
        assertThat(
            (attr.reader as AttributeReader<Long>).read(attr, createCsvCell("123"))
        ).isEqualTo(123L)
    }

    @Test
    internal fun attributeWithWriterPluginTest() {
        val attr = EntityType.attributes.getValue("test")
        assertThat(attr.writer).isNotNull.isInstanceOf(AttributeWriter::class.java)
        assertThat(testVal).isEqualTo(0)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<Long>).write(123L, createCsvCell("123"))
        assertThat(testVal).isEqualTo(123L)
    }

    private fun createCsvCell(@Suppress("SameParameterValue") value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
