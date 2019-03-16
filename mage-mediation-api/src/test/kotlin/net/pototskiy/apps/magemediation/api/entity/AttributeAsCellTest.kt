package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.AppCellDataException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.*

@Suppress("MagicNumber")
@DisplayName("Test Attribute cell")
@Execution(ExecutionMode.CONCURRENT)
internal class AttributeAsCellTest {

//    @BeforeEach
//    internal fun setUp() {
//        EntityTypeManager.currentManager = EntityTypeManager()
//    }

    @ParameterizedTest
    @MethodSource("testDataSource")
    internal fun attributeCellTest(data: TestData<*, *>) {
        val cell = AttributeAsCell(data.attr, data.wrappedValue)
        assertThat(cell.cellType).isEqualTo(data.cellType)
        CellType.values().filterNot { it == data.cellType }.forEach {
            assertThat(cell.cellType).isNotEqualTo(it)
        }
        assertThat(data.getter(cell)).isEqualTo(data.value)
//        assertThat(cell.stringValue).isEqualTo(data.stringValue)
        assertThat(cell.asString()).isEqualTo(data.value.toString())
        assertThatThrownBy { cell.row }.isInstanceOf(NotImplementedError::class.java)
        data.notCompatible.forEach {
            assertThatThrownBy { it(cell) }.isInstanceOf(AppCellDataException::class.java)
        }
    }

    @Test
    internal fun blankCellTest() {
        val cell = AttributeAsCell(stringAttr1, null)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        assertThat(cell.asString()).isEqualTo("")
        assertThatThrownBy { cell.stringValue }
            .isInstanceOf(AppCellDataException::class.java)
    }

    class TestData<T : Type, VT : Any>(
        val attr: Attribute<T>,
        val value: VT,
        val wrappedValue: T,
        val cellType: CellType,
        val getter: (Cell) -> VT,
        val stringValue: String,
        val notCompatible: List<(Cell) -> Unit>
    )

    companion object {
        private val typeManager = EntityTypeManager()

        private val dateFormat = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", null))!!
        private val dateTimeFormat = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", null))!!
        private val dateVal = dateFormat.parseDateTime(dateFormat.print(DateTime.now()))!!
        private val dateTimeVal = dateTimeFormat.parseDateTime(dateTimeFormat.print(DateTime.now()))!!
        private val stringAttr1 = typeManager.createAttribute("sAttr1", StringType::class)
        private val stringAttr2 = typeManager.createAttribute("sAttr2", StringType::class)
        private val boolTestData2 = TestData(
            typeManager.createAttribute("attr2", BooleanType::class) {
                writer(AttributeWriterWithFunction { value, cell ->
                    cell.setCellValue(value!!.value)
                })
            },
            false,
            BooleanType(false),
            CellType.BOOL,
            { it.booleanValue },
            false.toString(),
            listOf<(Cell) -> Unit>({ it.longValue }, { it.doubleValue }, { it.stringValue })
        )
        private val longTestData1 = TestData(
            typeManager.createAttribute("attr3", LongType::class) {
                writer(AttributeWriterWithFunction { value, cell ->
                    cell.setCellValue(value!!.value)
                })
            },
            111L,
            LongType(111L),
            CellType.LONG,
            { it.longValue },
            111L.toString(),
            listOf<(Cell) -> Unit>({ it.booleanValue }, { it.doubleValue }, { it.stringValue })
        )

        @Suppress("unused")
        @JvmStatic
        fun testDataSource(): Stream<TestData<*, *>> {
            return Stream.of(
                TestData(
                    typeManager.createAttribute("attr1", BooleanType::class) {
                        writer(AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        })
                    },
                    true,
                    BooleanType(true),
                    CellType.BOOL,
                    { it.booleanValue },
                    true.toString(),
                    listOf<(Cell) -> Unit>({ it.longValue }, { it.doubleValue }, { it.stringValue })
                ),
                boolTestData2,
                longTestData1,
                TestData(
                    typeManager.createAttribute("attr4", DoubleType::class) {
                        writer(AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        })
                    },
                    11.1,
                    DoubleType(11.1),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    11.1.toString(),
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.stringValue })
                ),
                TestData(
                    typeManager.createAttribute("attr5", StringType::class),
                    "test value",
                    StringType("test value"),
                    CellType.STRING,
                    { it.stringValue },
                    "test value",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr6", TextType::class),
                    "test value",
                    TextType("test value"),
                    CellType.STRING,
                    { it.stringValue },
                    "test value",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr7", DateType::class) {
                        writer(AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        })
                    },
                    HSSFDateUtil.getExcelDate(dateVal.toDate()),
                    DateType(dateVal),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    dateVal.toString(),
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.stringValue })
                ),
                TestData(
                    typeManager.createAttribute("attr8", DateTimeType::class) {
                        writer(AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        })
                    },
                    HSSFDateUtil.getExcelDate(dateTimeVal.toDate()),
                    DateTimeType(dateTimeVal),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    dateTimeVal.toString(),
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.stringValue })
                ),
                TestData(
                    typeManager.createAttribute("attr9", BooleanListType::class),
                    "1,0",
                    BooleanListType(listOf(BooleanType(true), BooleanType(false))),
                    CellType.STRING,
                    { it.stringValue },
                    "1,0",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr10", LongListType::class),
                    "1,2,3",
                    LongListType(listOf(LongType(1), LongType(2), LongType(3))),
                    CellType.STRING,
                    { it.stringValue },
                    "1,2,3",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr11", DoubleListType::class),
                    "1.1,2.2",
                    DoubleListType(listOf(DoubleType(1.1), DoubleType(2.2))),
                    CellType.STRING,
                    { it.stringValue },
                    "1.1,2.2",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr12", StringListType::class),
                    "\"str1\"\"\",\"str2\"\"\"",
                    StringListType(listOf(StringType("str1\""), StringType("str2\""))),
                    CellType.STRING,
                    { it.stringValue },
                    "\"str1\"\"\",\"str2\"\"\"",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr13", DateListType::class),
                    "${dateVal.toString(DateTimeFormat.forPattern("d.M.yy"))}," +
                            dateVal.toString(DateTimeFormat.forPattern("d.M.yy")),
                    DateListType(listOf(DateType(dateVal), DateType(dateVal))),
                    CellType.STRING,
                    { it.stringValue },
                    "$dateVal,$dateVal",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr14", DateTimeListType::class),
                    "${dateTimeVal.toString(DateTimeFormat.forPattern("d.M.yy H:m"))}," +
                            dateTimeVal.toString(DateTimeFormat.forPattern("d.M.yy H:m")),
                    DateTimeListType(listOf(DateTimeType(dateTimeVal), DateTimeType(dateTimeVal))),
                    CellType.STRING,
                    { it.stringValue },
                    "$dateTimeVal,$dateTimeVal",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr15", AttributeListType::class),
                    "sAttr1=false,sAttr2=111",
                    AttributeListType(
                        mapOf(
                            stringAttr1.name to AttributeAsCell(
                                stringAttr1,
                                StringType("false")
                            ),
                            stringAttr2.name to AttributeAsCell(
                                stringAttr2,
                                StringType("111")
                            )
                        )
                    ),
                    CellType.STRING,
                    { it.stringValue },
                    "sAttr1=false,sAttr2=111",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                )
            )
        }
    }
}
