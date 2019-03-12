package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.AttributeListValue
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.Workbook
import net.pototskiy.apps.magemediation.api.source.workbook.excel.ExcelWorkbook
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class DefaultAttributeListWriterTest {
    private lateinit var xlsWorkbook: HSSFWorkbook
    private lateinit var workbook: Workbook
    private lateinit var entity: EntityType
    private lateinit var attr: Attribute<AttributeListType>
    private lateinit var xlsTestDataCell: HSSFCell
    private lateinit var outputCell: Cell
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
        workbook = ExcelWorkbook(xlsWorkbook, false)
        outputCell = workbook["test-data"][0]!![0]!!
    }

    @AfterEach
    internal fun tearDown() {
        workbook.close()
    }

    @Test
    internal fun writeAttributeListToCellTest() {
        val wb = NestedAttributeWorkbook(null, ',', '"', '=', "test")
        wb.string = "attr1=value1,attr2=value2"
        val list = AttributeListValue(
            mapOf(
                "attr1" to wb[0][1]!![0]!!,
                "attr2" to wb[0][1]!![1]!!
            )
        )
        val writer = AttributeListStringWriter().apply {
            delimiter = ','
            valueQuote = '"'
            valueDelimiter = '='
        }
        writer.write(list, outputCell)
        assertThat(outputCell.stringValue).isEqualTo("attr1=value1,attr2=value2")
    }
}
