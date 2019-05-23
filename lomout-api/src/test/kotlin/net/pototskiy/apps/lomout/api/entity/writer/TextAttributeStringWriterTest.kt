package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.TextType
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File
import java.util.*
import kotlin.reflect.full.createInstance

@Execution(ExecutionMode.CONCURRENT)
internal class TextAttributeStringWriterTest {
    private lateinit var typeManager: EntityTypeManager
    private lateinit var file: File
    private lateinit var workbook: Workbook
    private lateinit var cell: Cell

    @BeforeEach
    internal fun setUp() {
        typeManager = EntityTypeManager()
        @Suppress("GraziInspection")
        file = File("../tmp/${UUID.randomUUID()}.xls")
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
        val attr = typeManager.createAttribute("attr", TextType::class)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<TextType>).write(TextType("test"), cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo("test")
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", TextType::class)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<TextType>).write(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[TextType::class]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(AttributeWriterWithPlugin::class.java)
        writer as AttributeWriterWithPlugin
        assertThat(writer.pluginClass).isEqualTo(TextAttributeStringWriter::class)
        val v = writer.pluginClass.createInstance() as TextAttributeStringWriter
        @Suppress("UNCHECKED_CAST")
        v.apply(writer.options as (TextAttributeStringWriter.() -> Unit))
    }
}
