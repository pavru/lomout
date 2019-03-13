package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.entity.AttributeWriter
import net.pototskiy.apps.magemediation.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.DoubleValue
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File
import java.util.*
import kotlin.reflect.full.createInstance

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DoubleAttributeStringWriterTest {
    private lateinit var typeManager: EntityTypeManager
    private lateinit var file: File
    private lateinit var workbook: Workbook
    private lateinit var cell: Cell

    @BeforeEach
    internal fun setUp() {
        typeManager = EntityTypeManager()
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
        val attr = typeManager.createAttribute("attr", DoubleType::class)
        val value = DoubleValue(111.222)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DoubleType>).write(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo("111.222")
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", DoubleType::class)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DoubleType>).write(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[DoubleType::class]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(AttributeWriterWithPlugin::class.java)
        writer as AttributeWriterWithPlugin
        assertThat(writer.pluginClass).isEqualTo(DoubleAttributeStringWriter::class)
        val v = writer.pluginClass.createInstance() as DoubleAttributeStringWriter
        @Suppress("UNCHECKED_CAST")
        v.apply(writer.options as (DoubleAttributeStringWriter.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
    }
}
