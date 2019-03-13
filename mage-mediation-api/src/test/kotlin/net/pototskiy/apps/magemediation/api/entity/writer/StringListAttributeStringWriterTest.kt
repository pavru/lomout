package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.entity.AttributeWriter
import net.pototskiy.apps.magemediation.api.entity.AttributeWriterWithPlugin
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringListValue
import net.pototskiy.apps.magemediation.api.entity.StringValue
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
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
    internal fun simpleWriteUnquotedTest() {
        val attr = typeManager.createAttribute("attr", StringListType::class) {
            writer(AttributeWriterWithPlugin(StringListAttributeStringWriter::class) {
                this as StringListAttributeStringWriter
                delimiter = ','
                quote = null
            })
        }
        val value = StringListValue(listOf(StringValue("test1"), StringValue("test2")))
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<StringListType>).write(value, cell)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.STRING)
        Assertions.assertThat(cell.stringValue).isEqualTo("test1,test2")
    }

    @Test
    internal fun simpleWriteQuotedTest() {
        val attr = typeManager.createAttribute("attr", StringListType::class) {
            writer(AttributeWriterWithPlugin(StringListAttributeStringWriter::class) {
                this as StringListAttributeStringWriter
                delimiter = ','
                quote = '\''
            })
        }
        val value = StringListValue(listOf(StringValue("test1"), StringValue("test2,")))
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<StringListType>).write(value, cell)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.STRING)
        Assertions.assertThat(cell.stringValue).isEqualTo("test1,'test2,'")
    }

    @Test
    internal fun writeNullValueTest() {
        val attr = typeManager.createAttribute("attr", StringListType::class)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<StringListType>).write(null, cell)
        Assertions.assertThat(cell.cellType).isEqualTo(CellType.BLANK)
    }

    @Test
    internal fun defaultWriterTest() {
        val writer = defaultWriters[StringListType::class]
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
