package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATELIST
import net.pototskiy.apps.lomout.api.entity.values.dateToString
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
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
internal class DateListAttributeStringWriterTest {
    private lateinit var typeManager: EntityTypeManagerImpl
    private lateinit var file: File
    private lateinit var workbook: Workbook
    private lateinit var cell: Cell

    @BeforeEach
    internal fun setUp() {
        typeManager = EntityTypeManagerImpl()
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
    internal fun simpleWriteUnquotedTest() {
        val attr = typeManager.createAttribute("attr", DATELIST::class,
            writer = AttributeWriterWithPlugin(DateListAttributeStringWriter::class) {
                this as DateListAttributeStringWriter
                delimiter = ','
                quote = null
            }
        )
        val now1 = DateTime.now()
        val now2 = DateTime.now().withFieldAdded(DurationFieldType.days(), 2)
        val value = DATELIST(listOf(DATE(now1), DATE(now2)))
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DATELIST>)(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo(
            "${now1.dateToString(DEFAULT_LOCALE)},${now2.dateToString(DEFAULT_LOCALE)}"
        )
    }

    @Test
    internal fun simpleWriteQuotedTest() {
        val attr = typeManager.createAttribute("attr", DATELIST::class,
            writer = AttributeWriterWithPlugin(DateListAttributeStringWriter::class) {
                this as DateListAttributeStringWriter
                delimiter = ','
                quote = '\''
                pattern = "d.M.yy"
            }
        )
        val now1 = DateTime.now()
        val now2 = DateTime.now().withFieldAdded(DurationFieldType.days(), 2)
        val value = DATELIST(listOf(DATE(now1), DATE(now2)))
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DATELIST>)(value, cell)
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(cell.stringValue).isEqualTo(
            "${now1.datetimeToString("d.M.yy")},${now2.datetimeToString("d.M.yy")}"
        )
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", DATELIST::class)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<DATELIST>)(null, cell)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[DATELIST::class]
        assertThat(writer).isNotNull
        assertThat(writer).isInstanceOf(AttributeWriterWithPlugin::class.java)
        writer as AttributeWriterWithPlugin
        assertThat(writer.pluginClass).isEqualTo(DateListAttributeStringWriter::class)
        val v = writer.pluginClass.createInstance() as DateListAttributeStringWriter
        @Suppress("UNCHECKED_CAST")
        v.apply(writer.options as (DateListAttributeStringWriter.() -> Unit))
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.quote).isNull()
    }
}
