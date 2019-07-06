package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
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

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class LongListAttributeStringWriterTest {
    @Suppress("unused")
    internal class TestType : Document() {
        @Writer(Attr1Writer::class)
        var attr1: List<Long> = emptyList()
        @Writer(Attr2Writer::class)
        var attr2: List<Long> = emptyList()
        var attr3: List<Long> = emptyList()

        companion object : DocumentMetadata(TestType::class)

        class Attr1Writer : WriterBuilder {
            override fun build(): AttributeWriter<out Any?> = createWriter<LongListAttributeStringWriter> {
                quote = null
                delimiter = ','
            }
        }

        class Attr2Writer : WriterBuilder {
            override fun build(): AttributeWriter<out Any?> = createWriter<LongListAttributeStringWriter> {
                quote = '\''
                delimiter = ','
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
    internal fun simpleWriteUnquotedTest() {
        val attr = TestType.attributes.getValue("attr1")
        val value = listOf(11L, 33L)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<List<Long>>).write(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo("11,33")
    }

    @Test
    internal fun simpleWriteQuotedTest() {
        val attr = TestType.attributes.getValue("attr2")
        val value = listOf(11L, 33L)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<List<Long>>).write(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo("11,33")
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = TestType.attributes.getValue("attr3")
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<List<Long>?>).write(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[SupportAttributeType.longListType]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(LongListAttributeStringWriter::class.java)
        writer as LongListAttributeStringWriter
        assertThat(writer.delimiter).isEqualTo(',')
        assertThat(writer.quote).isNull()
    }
}
