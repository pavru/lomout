package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType
import net.pototskiy.apps.lomout.api.entity.values.dateToString
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DateAttributeStringWriterTest {
    internal class TestType : Document() {
        var attr1: LocalDate = LocalDate.MIN
        @Writer(Attr2Writer::class)
        var attr2: LocalDate = LocalDate.MIN

        companion object : DocumentMetadata(TestType::class)
        class Attr2Writer : WriterBuilder {
            override fun build(): AttributeWriter<out Any?> = createWriter<DateAttributeStringWriter> {
                locale = "en_US"
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
        val now = LocalDate.now()
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LocalDate>).write(now, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue)
            .isEqualTo(now.format(DateTimeFormatter.ofPattern("d.M.uu")))
    }

    @Test
    internal fun simpleWriteLocaleTest() {
        val attr = TestType.attributes.getValue("attr2")
        val now = LocalDate.now()
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LocalDate>).write(now, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue)
            .isEqualTo(
                now.dateToString(DEFAULT_LOCALE)
            )
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = TestType.attributes.getValue("attr2")
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LocalDate?>).write(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[SupportAttributeType.dateType]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(DateAttributeStringWriter::class.java)
        writer as DateAttributeStringWriter
        assertThat(writer.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(writer.pattern).isEqualTo("d.M.uu")
    }
}
