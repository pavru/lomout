package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.text.ParseException

@Suppress("MagicNumber")
internal class DefaultBooleanReaderTest {

    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<BooleanType>

    @BeforeEach
    internal fun setUp() {
        EntityTypeManager.currentManager = EntityTypeManager()
        attr = EntityTypeManager.createAttribute("attr", BooleanType::class)
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
        val reader = BooleanAttributeReader().apply { locale = "en_US" }
        var cell = workbook["boolean"][1]!![1]!!
        assertThat(cell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, cell)?.value).isTrue()
        cell = workbook["boolean"][2]!![1]!!
        assertThat(cell.cellType).isEqualTo(CellType.DOUBLE)
        assertThat(reader.read(attr, cell)?.value).isFalse()
    }

    @Test
    internal fun readStringEnUsCorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "en_US" }
        var cell = workbook["boolean"][1]!![2]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, cell)?.value).isTrue()
        cell = workbook["boolean"][2]!![2]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, cell)?.value).isFalse()
    }

    @Test
    internal fun readStringEnUsIncorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "en_US" }
        var cell = workbook["boolean"][1]!![3]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, cell) }.isInstanceOf(ParseException::class.java)
        cell = workbook["boolean"][2]!![3]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, cell) }.isInstanceOf(ParseException::class.java)
    }

    @Test
    internal fun readStringRuRuCorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        var cell = workbook["boolean"][1]!![3]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, cell)?.value).isTrue()
        cell = workbook["boolean"][2]!![3]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, cell)?.value).isFalse()
    }

    @Test
    internal fun readStringRuRuIncorrectCellTest() {
        val readerEnUs = BooleanAttributeReader().apply { locale = "ru_RU" }
        var cell = workbook["boolean"][1]!![2]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, cell) }.isInstanceOf(ParseException::class.java)
        cell = workbook["boolean"][2]!![2]!!
        assertThat(cell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, cell) }.isInstanceOf(ParseException::class.java)
    }
}
