package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
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

@Execution(ExecutionMode.CONCURRENT)
internal class DefaultAttributeListReaderTest {
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<AttributeListType>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var inputCell: Cell
    private val entityTypeManager = EntityTypeManager()

    @BeforeEach
    internal fun setUp() {
        attr = entityTypeManager.createAttribute("attr", AttributeListType::class)
        entity = entityTypeManager.createEntityType("test", emptyList(), false).also {
            entityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr)))
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
    internal fun readeAttributeListTest() {
        val reader = AttributeListReader().apply {
            delimiter = ","
            quote = null
            valueDelimiter = "="
            valueQuote = "'"
        }
        xlsTestDataCell.setCellValue("attr1='value1',attr2='value2'")
        val list = reader.read(attr, inputCell)?.value
        assertThat(list).isNotNull
        assertThat(list?.keys)
            .hasSize(2).containsExactlyElementsOf(listOf("attr1", "attr2"))
        assertThat(list?.values?.map { it.asString() })
            .hasSize(2).containsExactlyElementsOf(listOf("value1", "value2"))
    }

    @Test
    internal fun readAttributeListDoubleCellTest() {
        val reader = AttributeListReader().apply {
            delimiter = ","
            quote = null
            valueDelimiter = "="
            valueQuote = "'"
        }
        xlsTestDataCell.setCellValue(1.1)
        assertThatThrownBy { reader.read(attr, inputCell) }.isInstanceOf(SourceException::class.java)
    }
}
