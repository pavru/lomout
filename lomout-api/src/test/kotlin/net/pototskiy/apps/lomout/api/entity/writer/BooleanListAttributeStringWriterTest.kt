package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BOOLEANLIST
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
import kotlin.reflect.full.createInstance

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class BooleanListAttributeStringWriterTest {
    private lateinit var typeManager: EntityTypeManagerImpl
    private lateinit var file: File
    private lateinit var workbook: Workbook
    private lateinit var cell: Cell
    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    internal fun setUp() {
        typeManager = EntityTypeManagerImpl()
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
        val attr = typeManager.createAttribute("attr", BOOLEANLIST::class,
            writer = AttributeWriterWithPlugin(BooleanListAttributeStringWriter::class) {
                this as BooleanListAttributeStringWriter
                delimiter = ','
                quote = null
            }
        )
        val value = BOOLEANLIST(listOf(BOOLEAN(true), BOOLEAN(false)))
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<BOOLEANLIST>)(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo("1,0")
    }

    @Test
    internal fun simpleWriteQuotedTest() {
        val attr = typeManager.createAttribute("attr", BOOLEANLIST::class,
            writer = AttributeWriterWithPlugin(BooleanListAttributeStringWriter::class) {
                this as BooleanListAttributeStringWriter
                delimiter = ','
                quote = '\''
            }
        )
        val value = BOOLEANLIST(listOf(BOOLEAN(true), BOOLEAN(false)))
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<BOOLEANLIST>)(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo("1,0")
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", BOOLEANLIST::class)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<BOOLEANLIST>)(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[BOOLEANLIST::class]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(AttributeWriterWithPlugin::class.java)
        writer as AttributeWriterWithPlugin
        assertThat(writer.pluginClass).isEqualTo(BooleanListAttributeStringWriter::class)
        val v = writer.pluginClass.createInstance() as BooleanListAttributeStringWriter
        @Suppress("UNCHECKED_CAST")
        v.apply(writer.options as (BooleanListAttributeStringWriter.() -> Unit))
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.quote).isNull()
    }
}
