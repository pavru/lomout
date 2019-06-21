package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.workbook.excel.ExcelWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.excel.setFileName
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.File

@Execution(ExecutionMode.CONCURRENT)
internal class DomainExceptionPlaceTest {
    private val manager = EntityTypeManagerImpl()
    @Suppress("UNCHECKED_CAST")
    private val attr1 = manager.createAttribute(
        "attr1",
        STRING::class,
        key = false,
        nullable = true,
        auto = false,
        builder = null,
        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
    )
    @Suppress("UNCHECKED_CAST")
    private val attr2 = manager.createAttribute(
        "attr2",
        STRING::class,
        key = false,
        nullable = true,
        auto = false,
        builder = null,
        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
    )
    private val entity2 = manager.createEntityType("entity2", false).also {
        manager.initialAttributeSetup(it, AttributeCollection(listOf(attr2)))
    }

    @org.junit.jupiter.api.Test
    fun attributeInfo() {
        assertThat(unknownPlace().attributeInfo()).isEqualTo("")
        val place = badPlace(attr1)
        assertThat(place.attributeInfo()).isEqualTo("A:'attr1'")
        @Suppress("UNUSED_VARIABLE")
        val entity1 = manager.createEntityType("entity1", false).also {
            manager.initialAttributeSetup(it, AttributeCollection(listOf(attr1)))
        }
        assertThat(place.attributeInfo()).isEqualTo("A:'attr1', E:'entity1'")
        val place2 = place + entity2
        assertThat(place2.attributeInfo()).isEqualTo("A:'attr1', E:'entity2'")
    }

    @org.junit.jupiter.api.Test
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
        assertThat((place + row + sheet).cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat((place + row + sheet + workbook).cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
        assertThat(badPlace(workbook).cellInfo()).isEqualTo("W:'test.xls'")
        assertThat((badPlace(workbook) + sheet).cellInfo()).isEqualTo("W:'test.xls', S:'sheet'")
        assertThat((badPlace(workbook) + sheet + row).cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4'")
        assertThat((badPlace(workbook) + sheet + row + cell).cellInfo()).isEqualTo("W:'test.xls', S:'sheet', R:'4', C:'6(F)'")
    }

    @org.junit.jupiter.api.Test
    fun fieldInfo() {
        assertThat(unknownPlace().fieldInfo()).isEqualTo("")
        val field = Field("field", 5, null, null)
        val place = badPlace(field)
        assertThat(place.fieldInfo()).isEqualTo("F:'field(6(F))'")
    }

    @org.junit.jupiter.api.Test
    fun dataInfo() {
        assertThat(unknownPlace().dataInfo()).isEqualTo("")
        assertThat(badData(56L).dataInfo()).isEqualTo("V:'56(Long)'")
    }

    @org.junit.jupiter.api.Test
    fun placeInfo() {
        assertThat(unknownPlace().fieldInfo()).isEqualTo("")
        val place = badData(5.6) + attr2
        assertThat(place.placeInfo()).isEqualTo("Place: A:'attr2', E:'entity2', V:'5.6(Double)'.")
    }
}
