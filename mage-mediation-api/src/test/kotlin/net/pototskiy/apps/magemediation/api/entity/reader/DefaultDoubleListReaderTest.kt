package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AttributeReaderWithPlugin
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.excel.ExcelWorkbook
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
internal class DefaultDoubleListReaderTest {
    private val typeManager = EntityTypeManager()
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<DoubleListType>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell

    @BeforeEach
    internal fun setUp() {
        attr = typeManager.createAttribute("attr", DoubleListType::class)
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
        val reader = DoubleListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue(1.1)
        assertThat(inputCell.cellType).isEqualTo(CellType.DOUBLE)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(PluginException::class.java)
    }

    @Test
    internal fun readStringEnUsCellTest() {
        val readerEnUs = DoubleListAttributeReader().apply { locale = "en_US" }
        xlsTestDataCell.setCellValue("1.1, 2.2,3.3")
        assertThat(inputCell.cellType).isEqualTo(CellType.STRING)
        assertThat(readerEnUs.read(attr, inputCell)?.value).isEqualTo(
            DoubleListType(
                listOf(
                    DoubleType(1.1), DoubleType(2.2), DoubleType(3.3)
                )
            )
        )
    }

    @Test
    internal fun defaultDoubleListReader() {
        @Suppress("UNCHECKED_CAST")
        val reader = defaultReaders[DoubleListType::class]
        assertThat(reader).isNotNull
        assertThat(reader).isInstanceOf(AttributeReaderWithPlugin::class.java)
        reader as AttributeReaderWithPlugin
        assertThat(reader.pluginClass).isEqualTo(DoubleListAttributeReader::class)
        val v = reader.pluginClass.createInstance() as DoubleListAttributeReader
        @Suppress("UNCHECKED_CAST")
        v.apply(reader.options as (DoubleListAttributeReader.() -> Unit))
        assertThat(v.locale).isEqualTo(DEFAULT_LOCALE_STR)
        assertThat(v.delimiter).isEqualTo(',')
        assertThat(v.quote).isNull()
    }
}
