package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LONGLIST
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvCell
import net.pototskiy.apps.lomout.api.source.workbook.csv.CsvInputWorkbook
import org.apache.commons.csv.CSVFormat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@Suppress("MagicNumber")
internal class AttributeReaderWriterTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)

    class TestReader : AttributeReaderPlugin<LONG>() {
        override fun read(attribute: Attribute<out LONG>, input: Cell): LONG? {
            return LONG(input.asString().toLong())
        }
    }

    companion object {
        var testVal = 0L
    }

    class TestWriter : AttributeWriterPlugin<LONG>() {
        override fun write(value: LONG?, cell: Cell) {
            testVal = value?.value!!
        }
    }

    @Test
    internal fun attributeWithReaderFunctionTest() {
        val attr = Attribute.Builder(helper, "test", LONG::class).apply {
            reader { _, _ ->
                LONG(234L)
            }
        }.build()
        assertThat(attr.reader).isNotNull.isInstanceOf(AttributeReaderWithFunction::class.java)
        @Suppress("UNCHECKED_CAST")
        assertThat(
            (attr.reader as AttributeReader<LONG>)(attr, createCsvCell("123"))
        ).isEqualTo(LONG(234L))
    }

    @Test
    internal fun attributeWithReaderPluginTest() {
        val attr = Attribute.Builder(helper, "test", LONG::class).apply {
            reader<TestReader>()
        }.build()
        assertThat(attr.reader).isNotNull.isInstanceOf(AttributeReaderWithPlugin::class.java)
        @Suppress("UNCHECKED_CAST")
        assertThat(
            (attr.reader as AttributeReader<LONG>)(attr, createCsvCell("123"))
        ).isEqualTo(LONG(123L))
    }

    @Test
    internal fun attributeWithWriterFunctionTest() {
        var v = 0L
        val attr = Attribute.Builder(helper, "test", LONG::class).apply {
            writer { value, _ ->
                v = value?.value!!
            }
        }.build()
        assertThat(attr.writer).isNotNull.isInstanceOf(AttributeWriterWithFunction::class.java)
        assertThat(v).isEqualTo(0)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LONG>)(LONG(123L), createCsvCell("123"))
        assertThat(v).isEqualTo(123L)
    }

    @Test
    internal fun attributeWithWriterPluginTest() {
        val attr = Attribute.Builder(helper, "test", LONG::class).apply {
            writer<TestWriter>()
        }.build()
        assertThat(attr.writer).isNotNull.isInstanceOf(AttributeWriterWithPlugin::class.java)
        assertThat(testVal).isEqualTo(0)
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LONG>)(LONG(123L), createCsvCell("123"))
        assertThat(testVal).isEqualTo(123L)
    }

    @Test
    internal fun ketNullableTest() {
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LONG::class).apply {
                key()
                nullable()
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Key attribute cannot be nullable")
    }

    @Test
    internal fun ketListTypeTest() {
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LONGLIST::class).apply {
                key()
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Key attribute cannot have list type or builder")
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LONGLIST::class).apply {
                key()
                builder {
                    LONGLIST(listOf(LONG(123L)))
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Key attribute cannot have list type or builder")
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LONG::class).apply {
                key()
                builder {
                    LONG(123L)
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Key attribute cannot have list type or builder")
    }

    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
