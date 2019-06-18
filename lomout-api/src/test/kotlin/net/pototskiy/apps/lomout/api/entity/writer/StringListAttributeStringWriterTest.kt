package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions
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
internal class StringListAttributeStringWriterTest {
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
        val attr = typeManager.createAttribute("attr", STRINGLIST::class,
            writer = AttributeWriterWithPlugin(StringListAttributeStringWriter::class) {
                this as StringListAttributeStringWriter
                delimiter = ','
                quote = null
            }
        )
        val value = STRINGLIST(listOf(STRING("test1"), STRING("test2")))
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<STRINGLIST>)(value, cell)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.STRING)
        Assertions.assertThat(cell.stringValue).isEqualTo("test1,test2")
    }

    @Test
    internal fun simpleWriteQuotedTest() {
        val attr = typeManager.createAttribute("attr", STRINGLIST::class,
            writer = AttributeWriterWithPlugin(StringListAttributeStringWriter::class) {
                this as StringListAttributeStringWriter
                delimiter = ','
                quote = '\''
            }
        )
        val value = STRINGLIST(listOf(STRING("test1"), STRING("test2,")))
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<STRINGLIST>)(value, cell)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.STRING)
        Assertions.assertThat(cell.stringValue).isEqualTo("test1,'test2,'")
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", STRINGLIST::class)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<STRINGLIST>)(null, cell)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[STRINGLIST::class]
        Assertions.assertThat(writer).isNotNull
        Assertions.assertThat(writer).isInstanceOf(AttributeWriterWithPlugin::class.java)
        writer as AttributeWriterWithPlugin
        Assertions.assertThat(writer.pluginClass).isEqualTo(StringListAttributeStringWriter::class)
        val v = writer.pluginClass.createInstance() as StringListAttributeStringWriter
        @Suppress("UNCHECKED_CAST")
        v.apply(writer.options as (StringListAttributeStringWriter.() -> Unit))
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.quote).isEqualTo('"')
    }
}
