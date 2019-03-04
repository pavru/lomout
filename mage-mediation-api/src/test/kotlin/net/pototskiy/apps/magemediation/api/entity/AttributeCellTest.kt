package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.*

@Suppress("MagicNumber")
@DisplayName("Test Attribute cell")
internal class AttributeCellTest {

    @BeforeEach
    internal fun setUp() {
        EntityTypeManager.cleanEntityTypeConfiguration()
        EntityAttributeManager.cleanEntityAttributeConfiguration()
    }

    @ParameterizedTest
    @MethodSource("testDataSource")
    internal fun attributeCellTest(data: TestData<*, *>) {
        val cell = AttributeCell(data.attr, data.wrappedValue)
        assertThat(cell.cellType).isEqualTo(data.cellType)
        CellType.values().filterNot { it == data.cellType }.forEach {
            assertThat(cell.cellType).isNotEqualTo(it)
        }
        assertThat(data.getter(cell)).isEqualTo(data.value)
        assertThat(cell.stringValue).isEqualTo(data.stringValue)
        assertThat(cell.asString()).isEqualTo(data.stringValue)
        assertThatThrownBy { cell.row }.isInstanceOf(NotImplementedError::class.java)
        data.notCompatible.forEach {
            assertThatThrownBy { it(cell) }.isInstanceOf(SourceException::class.java)
        }
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

        private val dateFormat = DateTimeFormat.shortDate()!!
        private val dateTimeFormat = DateTimeFormat.shortDateTime()
        private val dateVal = dateFormat.parseDateTime(dateFormat.print(DateTime.now()))!!
        private val dateTimeVal = dateTimeFormat.parseDateTime(dateTimeFormat.print(DateTime.now()))!!
        private val boolTestData2 = TestData(
            EntityAttributeManager.createAttribute("test:attr2", BooleanType::class),
            false,
            BooleanValue(false),
            CellType.BOOL,
            { it.booleanValue },
            "false",
            listOf<(Cell) -> Unit>({ it.longValue }, { it.doubleValue })
        )
        private val longTestData1 = TestData(
            EntityAttributeManager.createAttribute("test:attr3", LongType::class),
            111L,
            LongValue(111L),
            CellType.LONG,
            { it.longValue },
            "111",
            listOf<(Cell) -> Unit>({ it.booleanValue }, { it.doubleValue })
        )

        @JvmStatic
        fun testDataSource(): Stream<TestData<*, *>> {
            return Stream.of(
                TestData(
                    EntityAttributeManager.createAttribute("test:attr1", BooleanType::class),
                    true,
                    BooleanValue(true),
                    CellType.BOOL,
                    { it.booleanValue },
                    "true",
                    listOf<(Cell) -> Unit>({ it.longValue }, { it.doubleValue })
                ),
                boolTestData2,
                longTestData1,
                TestData(
                    EntityAttributeManager.createAttribute("test:attr4", DoubleType::class),
                    11.1,
                    DoubleValue(11.1),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    "11.1",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr5", StringType::class),
                    "test value",
                    StringValue("test value"),
                    CellType.STRING,
                    { it.stringValue },
                    "test value",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr6", TextType::class),
                    "test value",
                    TextValue("test value"),
                    CellType.STRING,
                    { it.stringValue },
                    "test value",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr7", DateType::class),
                    HSSFDateUtil.getExcelDate(dateVal.toDate()),
                    DateValue(dateVal),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    dateVal.toString(),
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr8", DateTimeType::class),
                    HSSFDateUtil.getExcelDate(dateTimeVal.toDate()),
                    DateTimeValue(dateTimeVal),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    dateTimeVal.toString(),
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr9", BooleanListType::class),
                    "true,false",
                    BooleanListValue(listOf(BooleanValue(true), BooleanValue(false))),
                    CellType.STRING,
                    { it.stringValue },
                    "true,false",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr10", LongListType::class),
                    "1,2,3",
                    LongListValue(listOf(LongValue(1), LongValue(2), LongValue(3))),
                    CellType.STRING,
                    { it.stringValue },
                    "1,2,3",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr11", DoubleListType::class),
                    "1.1,2.2",
                    DoubleListValue(listOf(DoubleValue(1.1), DoubleValue(2.2))),
                    CellType.STRING,
                    { it.stringValue },
                    "1.1,2.2",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr12", StringListType::class),
                    "str1\",str2\"",
                    StringListValue(listOf(StringValue("str1\""), StringValue("str2\""))),
                    CellType.STRING,
                    { it.stringValue },
                    "str1\",str2\"",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr13", DateListType::class),
                    "$dateVal,$dateVal",
                    DateListValue(listOf(DateValue(dateVal), DateValue(dateVal))),
                    CellType.STRING,
                    { it.stringValue },
                    "$dateVal,$dateVal",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr14", DateTimeListType::class),
                    "$dateTimeVal,$dateTimeVal",
                    DateTimeListValue(listOf(DateTimeValue(dateTimeVal), DateTimeValue(dateTimeVal))),
                    CellType.STRING,
                    { it.stringValue },
                    "$dateTimeVal,$dateTimeVal",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    EntityAttributeManager.createAttribute("test:attr15", AttributeListType::class),
                    "attr2=\"false\",attr3=\"111\"",
                    AttributeListValue(mapOf(
                        boolTestData2.attr.name.attributeName to AttributeCell(
                            boolTestData2.attr,
                            boolTestData2.wrappedValue
                        ),
                        longTestData1.attr.name.attributeName to AttributeCell(
                            longTestData1.attr,
                            longTestData1.wrappedValue
                        )
                    )),
                    CellType.STRING,
                    { it.stringValue },
                    "attr2=\"false\",attr3=\"111\"",
                    listOf<(Cell) -> Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                )
            )
        }
    }
}
