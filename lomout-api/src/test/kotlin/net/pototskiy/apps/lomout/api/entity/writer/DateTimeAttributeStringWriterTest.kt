package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
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
internal class DateTimeAttributeStringWriterTest {
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
    internal fun simpleWriteTest() {
        val attr = typeManager.createAttribute("attr", DATETIME::class)
        val now = DateTime.now()
        val value = DATETIME(now)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DATETIME>)(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue)
            .isEqualTo(now.toString(DateTimeFormat.forPattern("d.M.yy H:m")))
    }

    @Test
    internal fun simpleWriteLocaleTest() {
        val attr = typeManager.createAttribute("attr", DATETIME::class,
            writer = AttributeWriterWithPlugin(
                DateTimeAttributeStringWriter::class
            ) {
                this as DateTimeAttributeStringWriter
                pattern = null
            }
        )
        val now = DateTime.now()
        val value = DATETIME(now)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DATETIME>)(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue)
            .isEqualTo(
                now.toString(
                    DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", DEFAULT_LOCALE))
                )
            )
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", DATETIME::class)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DATETIME>)(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[DATETIME::class]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(AttributeWriterWithPlugin::class.java)
        writer as AttributeWriterWithPlugin
        assertThat(writer.pluginClass).isEqualTo(DateTimeAttributeStringWriter::class)
        val v = writer.pluginClass.createInstance() as DateTimeAttributeStringWriter
        @Suppress("UNCHECKED_CAST")
        v.apply(writer.options as (DateTimeAttributeStringWriter.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(v.pattern).isEqualTo("d.M.yy H:m")
    }
}
