package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class DefaultDateReaderTest {
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<DateType>

    @BeforeEach
    internal fun setUp() {
        EntityTypeManager.currentManager = EntityTypeManager()
        attr = EntityTypeManager.createAttribute("attr", DateType::class)
        entity = EntityTypeManager.createEntityType("test", emptyList(), false).also {
            EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr)))
        }
        workbook = WorkbookFactory.create(
            File("${System.getenv("TEST_DATA_DIR")}/readers.test.data.xls").toURI().toURL()
        )
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
    }

    @Test
    internal fun readDoubleCellTest() {
        val expected = DateTimeFormat.forPattern("d.M.YY").parseDateTime("15.03.31")
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        var cell = workbook["date"][1]!![0]!!
        assertThat(cell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
        cell = workbook["date"][1]!![1]!!
        assertThat(cell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readStringCellTest() {
        val expected = DateTimeFormat.forPattern("d.M.YY").parseDateTime("15.03.31")
        val readerEnUs = DateAttributeReader().apply { locale = "en_US" }
        val readerRuRu = DateAttributeReader().apply { locale = "ru_RU" }
        var cell = workbook["date"][1]!![2]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, cell) }.isInstanceOf(SourceException::class.java)
        cell = workbook["date"][1]!![3]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, cell) }.isInstanceOf(SourceException::class.java)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
    }

    @Test
    internal fun readStringCellWithPatternTest() {
        val expected = DateTimeFormat.forPattern("d.M.YY").parseDateTime("15.03.31")
        val readerEnUs = DateAttributeReader().apply { pattern = "M/d/YY" }
        val readerRuRu = DateAttributeReader().apply { pattern = "d.M.YY" }
        var cell = workbook["date"][1]!![2]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, cell)?.value).isEqualTo(expected)
        assertThatThrownBy { readerRuRu.read(attr, cell) }.isInstanceOf(SourceException::class.java)
        cell = workbook["date"][1]!![3]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, cell) }.isInstanceOf(SourceException::class.java)
        assertThat(readerRuRu.read(attr, cell)?.value).isEqualTo(expected)
    }
}
