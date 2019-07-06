package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.documentMetadata
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.setFileName
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class DomainExceptionPlaceTest {

    class EntityType1 : Document() {
        var attr1: String? = null

        companion object : DocumentMetadata(EntityType1::class)
    }

    class EntityType2 : Document() {
        var attr2: String? = null

        companion object : DocumentMetadata(EntityType2::class)
    }

    private val attr1 = EntityType1::class.documentMetadata.attributes.getValue(EntityType1::attr1.name)
    private val attr2 = EntityType2::class.documentMetadata.attributes.getValue(EntityType2::attr2.name)

    @Test
    internal fun attributeInfo() {
        assertThat(unknownPlace().attributeInfo()).isEqualTo("")
        val place = badPlace(attr1)
        assertThat(place.attributeInfo()).isEqualTo("A:'attr1', E:'EntityType1'")
        val place2 = place + EntityType2::class
        assertThat(place2.attributeInfo()).isEqualTo("A:'attr1', E:'EntityType2'")
    }

    @Test
    fun cellInfo() {
        assertThat(unknownPlace().cellInfo()).isEqualTo("")
        val excelWorkbook = HSSFWorkbook()
        excelWorkbook.setFileName(File("test.xls"))
        val workbook = ExcelWorkbook(excelWorkbook)
        val excelSheet = excelWorkbook.createSheet("sheet")
        val sheet = workbook["sheet"]
        val excelRow = excelSheet.createRow(3)
        val row = sheet[3]
        excelRow.createCell(5)
        val cell = row!![5]
        val place = badPlace(cell!!)
        assertThat(place.cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row).cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row + sheet).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row + sheet + workbook).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat(badPlace(workbook).cellInfo()).isEqualTo("W:'test.xls'")
        assertThat((badPlace(workbook) + sheet).cellInfo()).isEqualTo("W:'test.xls', S:'sheet'")
        assertThat((badPlace(workbook) + sheet + row).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4'")
        assertThat((badPlace(workbook) + sheet + row + cell).cellInfo())
            .isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
    }

    @Test
    fun fieldInfo() {
        assertThat(unknownPlace().fieldInfo()).isEqualTo("")
        val field = Field("field", 5, null)
        val place = badPlace(field)
        assertThat(place.fieldInfo()).isEqualTo("F:'field(6(F))'")
    }

    @Test
    fun dataInfo() {
        assertThat(unknownPlace().dataInfo()).isEqualTo("")
        assertThat(badData(56L).dataInfo()).isEqualTo("V:'56(Long)'")
    }

    @Test
    fun placeInfo() {
        assertThat(unknownPlace().fieldInfo()).isEqualTo("")
        val place = badData(5.6) + attr2
        assertThat(place.placeInfo()).isEqualTo("Place: A:'attr2', E:'EntityType2', V:'5.6(Double)'.")
    }
}
