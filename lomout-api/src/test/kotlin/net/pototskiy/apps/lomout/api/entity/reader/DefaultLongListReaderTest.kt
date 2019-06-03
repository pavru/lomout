package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.LongListType
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.reflect.full.createInstance

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DefaultLongListReaderTest {
    private val typeManager = EntityTypeManager()
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<LongListType>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        attr = typeManager.createAttribute("attr", LongListType::class)
        entity = typeManager.createEntityType("test", emptyList(), false).also {
            typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr)))
        }
        xlsWorkbook = HSSFWorkbookFactory.createWorkbook()
        val xlsSheet = xlsWorkbook.createSheet("test-data")
        xlsSheet.isActive = true
        xlsTestDataCell = xlsSheet.createRow(0).createCell(0)
        workbook = ExcelWorkbook(xlsWorkbook)
        inputCell = workbook["test-data"][0]!![0]!!
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
    }

    @Test
    internal fun readDoubleCellTest() {
        val reader = LongListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(1.1)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
    }

    @Test
    internal fun readStringEnUsCellTest() {
        val readerEnUs = LongListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("11, 22,33")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(
            LongListType(
                listOf(
                    LongType(11L), LongType(22L), LongType(33L)
                )
            )
        )
        xlsTestDataCell.setCellValue("11, 22,A")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThatThrownBy { readerEnUs.read(attr, inputCell) }.isInstanceOf(AppDataException::class.java)
    }

    @Test
    internal fun defaultLongListReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[LongListType::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(LongListAttributeReader::class)
        val v = reader.pluginClass.createInstance() as LongListAttributeReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (LongListAttributeReader.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.quote).isNull()
    }
}
