package net.pototskiy.apps.magemediation.api.source.nested

import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class NestedAttributeSheetTest {
    @Test
    internal fun insertAndGetRowTest() {
        val workbook = NestedAttributeWorkbook(null, ",", null, "=", "test")
        workbook.string = "attr1=value1,attr1=value1"
        val sheet = workbook[0]
        assertThat(sheet[0]?.toList()).containsExactlyElementsOf(sheet.insertRow(0).toList())
        assertThat(sheet[1]?.toList()).containsExactlyElementsOf(sheet.insertRow(1).toList())
        assertThatThrownBy { sheet[2] }
            .isInstanceOf(SourceException::class.java)
            .hasMessageContaining("Attribute workbook has only 2 rows")
        assertThatThrownBy { sheet.insertRow(2) }
            .isInstanceOf(SourceException::class.java)
            .hasMessageContaining("Attribute workbook has only 2 rows")
    }

    @Test
    internal fun sheetNameTest() {
        val workbook = NestedAttributeWorkbook(null, ",", null, "=", "test")
        workbook.string = "attr1=value1,attr1=value1"
        assertThat(workbook[0].name).isEqualTo("default")
    }
}
