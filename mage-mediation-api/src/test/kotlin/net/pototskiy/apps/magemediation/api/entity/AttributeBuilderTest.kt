package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.csv.CsvCell
import net.pototskiy.apps.magemediation.api.source.workbook.csv.CsvInputWorkbook
import org.apache.commons.csv.CSVFormat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.dao.EntityID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@Suppress("MagicNumber")
internal class AttributeBuilderTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)

    class TestBuilder : AttributeBuilderPlugin<LongType>() {
        var returnValue: Long = 0L
        override fun build(entity: DbEntity): LongType? {
            return LongType(returnValue)
        }
    }

    class TestReader : AttributeReaderPlugin<LongType>() {
        override fun read(attribute: Attribute<out LongType>, input: Cell): LongType? {
            return LongType(input.asString().toLong())
        }
    }

    companion object {
        var testVal = 0L
    }

    class TestWriter : AttributeWriterPlugin<LongType>() {
        override fun write(value: LongType?, cell: Cell) {
            testVal = value?.value!!
        }
    }

    @Test
    internal fun attributeWithBuildFunctionTest() {
        val attr = Attribute.Builder(helper, "test", LongType::class).apply {
            builder {
                LongType(123L)
            }
        }.build()
        assertThat(attr.builder).isNotNull.isInstanceOf(AttributeBuilderWithFunction::class.java)
        assertThat(attr.builder?.build(DbEntity(EntityID(1, DbEntityTable)))).isEqualTo(LongType(123L))
    }

    @Test
    internal fun attributeWithBuildPluginTest() {
        val attr = Attribute.Builder(helper, "test", LongType::class).apply {
            builder<TestBuilder> {
                returnValue = 321L
            }
        }.build()
        assertThat(attr.builder).isNotNull.isInstanceOf(AttributeBuilderWithPlugin::class.java)
        assertThat(attr.builder?.build(DbEntity(EntityID(1, DbEntityTable)))).isEqualTo(LongType(321L))
    }

    @Test
    internal fun attributeWithReaderFunctionTest() {
        val attr = Attribute.Builder(helper, "test", LongType::class).apply {
            reader { _, _ ->
                LongType(234L)
            }
        }.build()
        assertThat(attr.reader).isNotNull.isInstanceOf(AttributeReaderWithFunction::class.java)
        @Suppress("UNCHECKED_CAST")
        assertThat(
            (attr.reader as AttributeReader<LongType>).read(attr, createCsvCell("123"))
        ).isEqualTo(LongType(234L))
    }

    @Test
    internal fun attributeWithReaderPluginTest() {
        val attr = Attribute.Builder(helper, "test", LongType::class).apply {
            reader<TestReader>()
        }.build()
        assertThat(attr.reader).isNotNull.isInstanceOf(AttributeReaderWithPlugin::class.java)
        @Suppress("UNCHECKED_CAST")
        assertThat(
            (attr.reader as AttributeReader<LongType>).read(attr, createCsvCell("123"))
        ).isEqualTo(LongType(123L))
    }

    @Test
    internal fun attributeWithWriterFunctionTest() {
        var v = 0L
        val attr = Attribute.Builder(helper, "test", LongType::class).apply {
            writer { value, _ ->
                v = value?.value!!
            }
        }.build()
        assertThat(attr.writer).isNotNull.isInstanceOf(AttributeWriterWithFunction::class.java)
        assertThat(v).isZero()
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LongType>).write(LongType(123L), createCsvCell("123"))
        assertThat(v).isEqualTo(123L)
    }

    @Test
    internal fun attributeWithWriterPluginTest() {
        val attr = Attribute.Builder(helper, "test", LongType::class).apply {
            writer<TestWriter>()
        }.build()
        assertThat(attr.writer).isNotNull.isInstanceOf(AttributeWriterWithPlugin::class.java)
        assertThat(testVal).isZero()
        @Suppress("UNCHECKED_CAST")
        (attr.writer as AttributeWriter<LongType>).write(LongType(123L), createCsvCell("123"))
        assertThat(testVal).isEqualTo(123L)
    }

    @Test
    internal fun ketNullableTest() {
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LongType::class).apply {
                key()
                nullable()
            }.build()
        }.isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Key attribute can not be nullable")
    }

    @Test
    internal fun ketListTypeTest() {
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LongListType::class).apply {
                key()
            }.build()
        }.isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Key attribute can not have list type or builder")
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LongListType::class).apply {
                key()
                builder {
                    LongListType(listOf(LongType(123L)))
                }
            }.build()
        }.isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Key attribute can not have list type or builder")
        assertThatThrownBy {
            Attribute.Builder(helper, "test", LongType::class).apply {
                key()
                builder {
                    LongType(123L)
                }
            }.build()
        }.isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Key attribute can not have list type or builder")
    }

    private fun createCsvCell(value: String): CsvCell {
        val reader = value.byteInputStream().reader()
        CsvInputWorkbook(reader, CSVFormat.RFC4180).use {
            return it[0][0][0]!!
        }
    }
}
