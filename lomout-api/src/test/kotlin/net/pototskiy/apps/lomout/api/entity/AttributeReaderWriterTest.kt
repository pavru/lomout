package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.plugable.Reader
import net.pototskiy.apps.lomout.api.plugable.ReaderBuilder
import net.pototskiy.apps.lomout.api.plugable.Writer
import net.pototskiy.apps.lomout.api.plugable.WriterBuilder
import net.pototskiy.apps.lomout.api.plugable.createReader
import net.pototskiy.apps.lomout.api.plugable.createWriter
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
