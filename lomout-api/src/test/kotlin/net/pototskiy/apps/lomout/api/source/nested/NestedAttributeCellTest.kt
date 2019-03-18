package net.pototskiy.apps.lomout.api.source.nested

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.source.workbook.CellAddress
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class NestedAttributeCellTest {
    @Test
    internal fun getCellAddressTest() {
        val workbook = NestedAttributeWorkbook(null, ',', null, '=', "test")
        workbook.string = "attr1=value1,attr2=value2"
        val cell = workbook[0][1]!![1]
        assertThat(cell).isNotNull
        assertThat(cell?.address).isEqualTo(CellAddress(1, 1))
        assertThat(cell?.row?.rowNum).isEqualTo(1)
    }

    @Test
    internal fun notAllowOperationsTest() {
        val workbook = NestedAttributeWorkbook(null, ',', null, '=', "test")
        workbook.string = "attr1=value1,attr2=value2"
        val cell = workbook[0][1]!![1]
        assertThatThrownBy { cell?.booleanValue }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.longValue }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.doubleValue }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.doubleValue }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(true) }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(1L) }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(1.1) }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
        assertThatThrownBy { cell?.setCellValue(DateTime.now()) }
            .isInstanceOf(AppCellDataException::class.java)
            .hasMessageContaining("${NestedAttributeCell::class.simpleName} supports only string type value")
    }
}
