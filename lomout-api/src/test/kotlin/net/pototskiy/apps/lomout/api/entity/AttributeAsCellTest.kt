package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BOOLEANLIST
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATELIST
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DATETIMELIST
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.DOUBLELIST
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LONGLIST
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
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
//        EntityTypeManagerImpl.currentManager = EntityTypeManagerImpl()
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
            assertThatThrownBy { it(cell) }.isInstanceOf(AppDataException::class.java)
        }
    }

    @Test
    internal fun blankCellTest() {
        val cell = AttributeAsCell(stringAttr1, null)
        assertThat(cell.cellType).isEqualTo(CellType.BLANK)
        assertThat(cell.asString()).isEqualTo("")
        assertThatThrownBy { cell.stringValue }
            .isInstanceOf(AppDataException::class.java)
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

    @Suppress("RemoveExplicitTypeArguments")
    companion object {
        private val typeManager = EntityTypeManagerImpl()

        private val dateFormat = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", null))!!
        private val dateTimeFormat = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("SS", null))!!
        private val dateVal = dateFormat.parseDateTime(dateFormat.print(DateTime.now()))!!
        private val dateTimeVal = dateTimeFormat.parseDateTime(dateTimeFormat.print(DateTime.now()))!!
        @Suppress("UNCHECKED_CAST")
        private val stringAttr1 = typeManager.createAttribute(
            "sAttr1", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        private val stringAttr2 = typeManager.createAttribute(
            "sAttr2", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        private val boolTestData2 = TestData(
            typeManager.createAttribute("attr2", BOOLEAN::class,
                builder = null,
                reader = defaultReaders[BOOLEAN::class] as AttributeReader<out BOOLEAN>,
                writer = AttributeWriterWithFunction { value, cell ->
                    cell.setCellValue(value!!.value)
                }),
            false,
            BOOLEAN(false),
            CellType.BOOL,
            { it.booleanValue },
            false.toString(),
            listOf<(Cell)->Unit>({ it.longValue }, { it.doubleValue }, { it.stringValue })
        )
        @Suppress("UNCHECKED_CAST")
        private val longTestData1 = TestData(
            typeManager.createAttribute("attr3", LONG::class,
                builder = null,
                reader = defaultReaders[LONG::class] as AttributeReader<out LONG>,
                writer = AttributeWriterWithFunction { value, cell ->
                    cell.setCellValue(value!!.value)
                }),
            111L,
            LONG(111L),
            CellType.LONG,
            { it.longValue },
            111L.toString(),
            listOf<(Cell)->Unit>({ it.booleanValue }, { it.doubleValue }, { it.stringValue })
        )

        @Suppress("unused", "LongMethod")
        @JvmStatic
        fun testDataSource(): Stream<TestData<*, *>> {
            @Suppress("UNCHECKED_CAST")
            return Stream.of(
                TestData(
                    typeManager.createAttribute("attr1", BOOLEAN::class,
                        builder = null,
                        reader = defaultReaders[BOOLEAN::class] as AttributeReader<out BOOLEAN>,
                        writer = AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        }),
                    true,
                    BOOLEAN(true),
                    CellType.BOOL,
                    { it.booleanValue },
                    true.toString(),
                    listOf<(Cell)->Unit>({ it.longValue }, { it.doubleValue }, { it.stringValue })
                ),
                boolTestData2,
                longTestData1,
                TestData(
                    typeManager.createAttribute("attr4", DOUBLE::class,
                        builder = null,
                        reader = defaultReaders[DOUBLE::class] as AttributeReader<out DOUBLE>,
                        writer = AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        }),
                    11.1,
                    DOUBLE(11.1),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    11.1.toString(),
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.stringValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr5", STRING::class,
                        builder = null,
                        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
                        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
                    ),
                    "test value",
                    STRING("test value"),
                    CellType.STRING,
                    { it.stringValue },
                    "test value",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr6", TEXT::class,
                        builder = null,
                        reader = defaultReaders[TEXT::class] as AttributeReader<out TEXT>,
                        writer = defaultWriters[TEXT::class] as AttributeWriter<out TEXT>
                    ),
                    "test value",
                    TEXT("test value"),
                    CellType.STRING,
                    { it.stringValue },
                    "test value",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute("attr7", DATE::class,
                        builder = null,
                        reader = defaultReaders[DATE::class] as AttributeReader<out DATE>,
                        writer = AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        }),
                    HSSFDateUtil.getExcelDate(dateVal.toDate()),
                    DATE(dateVal),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    dateVal.toString(),
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.stringValue })
                ),
                TestData(
                    typeManager.createAttribute("attr8", DATETIME::class,
                        builder = null,
                        reader = defaultReaders[DATETIME::class] as AttributeReader<out DATETIME>,
                        writer = AttributeWriterWithFunction { value, cell ->
                            cell.setCellValue(value!!.value)
                        }),
                    HSSFDateUtil.getExcelDate(dateTimeVal.toDate()),
                    DATETIME(dateTimeVal),
                    CellType.DOUBLE,
                    { it.doubleValue },
                    dateTimeVal.toString(),
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.stringValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr9", BOOLEANLIST::class,
                        builder = null,
                        reader = defaultReaders[BOOLEANLIST::class] as AttributeReader<out BOOLEANLIST>,
                        writer = defaultWriters[BOOLEANLIST::class] as AttributeWriter<out BOOLEANLIST>
                    ),
                    "1,0",
                    BOOLEANLIST(listOf(BOOLEAN(true), BOOLEAN(false))),
                    CellType.STRING,
                    { it.stringValue },
                    "1,0",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr10", LONGLIST::class,
                        builder = null,
                        reader = defaultReaders[LONGLIST::class] as AttributeReader<out LONGLIST>,
                        writer = defaultWriters[LONGLIST::class] as AttributeWriter<out LONGLIST>
                    ),
                    "1,2,3",
                    LONGLIST(listOf(LONG(1), LONG(2), LONG(3))),
                    CellType.STRING,
                    { it.stringValue },
                    "1,2,3",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr11", DOUBLELIST::class,
                        builder = null,
                        reader = defaultReaders[DOUBLELIST::class] as AttributeReader<out DOUBLELIST>,
                        writer = defaultWriters[DOUBLELIST::class] as AttributeWriter<out DOUBLELIST>
                    ),
                    "1.1,2.2",
                    DOUBLELIST(listOf(DOUBLE(1.1), DOUBLE(2.2))),
                    CellType.STRING,
                    { it.stringValue },
                    "1.1,2.2",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr12", STRINGLIST::class,
                        builder = null,
                        reader = defaultReaders[STRINGLIST::class] as AttributeReader<out STRINGLIST>,
                        writer = defaultWriters[STRINGLIST::class] as AttributeWriter<out STRINGLIST>
                    ),
                    "\"str1\"\"\",\"str2\"\"\"",
                    STRINGLIST(listOf(STRING("str1\""), STRING("str2\""))),
                    CellType.STRING,
                    { it.stringValue },
                    "\"str1\"\"\",\"str2\"\"\"",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr13", DATELIST::class,
                        builder = null,
                        reader = defaultReaders[DATELIST::class] as AttributeReader<out DATELIST>,
                        writer = defaultWriters[DATELIST::class] as AttributeWriter<out DATELIST>
                    ),
                    "${dateVal.toString(DateTimeFormat.forPattern("d.M.yy"))}," +
                            dateVal.toString(DateTimeFormat.forPattern("d.M.yy")),
                    DATELIST(listOf(DATE(dateVal), DATE(dateVal))),
                    CellType.STRING,
                    { it.stringValue },
                    "$dateVal,$dateVal",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr14", DATETIMELIST::class,
                        builder = null,
                        reader = defaultReaders[DATETIMELIST::class] as AttributeReader<out DATETIMELIST>,
                        writer = defaultWriters[DATETIMELIST::class] as AttributeWriter<out DATETIMELIST>
                    ),
                    "${dateTimeVal.toString(DateTimeFormat.forPattern("d.M.yy H:m"))}," +
                            dateTimeVal.toString(DateTimeFormat.forPattern("d.M.yy H:m")),
                    DATETIMELIST(listOf(DATETIME(dateTimeVal), DATETIME(dateTimeVal))),
                    CellType.STRING,
                    { it.stringValue },
                    "$dateTimeVal,$dateTimeVal",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                ),
                TestData(
                    typeManager.createAttribute(
                        "attr15", ATTRIBUTELIST::class,
                        builder = null,
                        reader = defaultReaders[ATTRIBUTELIST::class] as AttributeReader<out ATTRIBUTELIST>,
                        writer = defaultWriters[ATTRIBUTELIST::class] as AttributeWriter<out ATTRIBUTELIST>
                    ),
                    "sAttr1=false,sAttr2=111",
                    ATTRIBUTELIST(
                        mapOf(
                            stringAttr1.name to AttributeAsCell(stringAttr1, STRING("false")),
                            stringAttr2.name to AttributeAsCell(stringAttr2, STRING("111"))
                        )
                    ),
                    CellType.STRING,
                    { it.stringValue },
                    "sAttr1=false,sAttr2=111",
                    listOf<(Cell)->Unit>({ it.booleanValue }, { it.longValue }, { it.doubleValue })
                )
            )
        }
    }
}
