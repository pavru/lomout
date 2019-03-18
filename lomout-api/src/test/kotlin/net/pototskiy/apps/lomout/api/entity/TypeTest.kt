package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber", "TooManyFunctions")
@Execution(ExecutionMode.CONCURRENT)
internal class TypeTest {
    @Test
    internal fun sqlTypeTest() {
        assertThat(StringType("").sqlType()).isEqualTo(VarCharColumnType::class)
        assertThatThrownBy { StringType("", true).sqlType() }
            .isInstanceOf(AppDataException::class.java)
        assertThatThrownBy { AttributeListType(emptyMap()).sqlType() }
            .isInstanceOf(AppDataException::class.java)
        assertThatThrownBy { AttributeListType(emptyMap(), false).sqlType() }
            .isInstanceOf(AppDataException::class.java)
        assertThat(StringType::class.sqlType()).isEqualTo(VarCharColumnType::class)
        assertThatThrownBy { AttributeListType::class.sqlType() }.isInstanceOf(AppDataException::class.java)
    }

    @Test
    internal fun isSingleTest() {
        assertThat(StringType::class.isSingle()).isTrue()
        assertThat(StringListType::class.isSingle()).isFalse()
        assertThat(AttributeListType::class.isSingle()).isFalse()
        assertThat(StringType("").isSingle()).isTrue()
        assertThat(StringListType(emptyList()).isSingle()).isFalse()
        assertThat(AttributeListType(emptyMap()).isSingle()).isFalse()
    }

    @Test
    internal fun isListTest() {
        assertThat(StringListType::class.isList()).isTrue()
        assertThat(StringType::class.isList()).isFalse()
        assertThat(StringListType(emptyList()).isList()).isTrue()
        assertThat(StringType("").isList()).isFalse()
    }

    @Test
    internal fun isMapTest() {
        assertThat(AttributeListType::class.isMap()).isTrue()
        assertThat(StringType::class.isMap()).isFalse()
        assertThat(AttributeListType(emptyMap()).isMap()).isTrue()
        assertThat(StringType("").isMap()).isFalse()
    }

    @Test
    internal fun isTypeOfTest() {
        assertThat(StringType::class.isTypeOf<StringType>()).isTrue()
        assertThat(StringType("").isTypeOf<StringType>()).isTrue()
        assertThat(StringType::class.isTypeOf<LongType>()).isFalse()
        assertThat(StringType("").isTypeOf<LongType>()).isFalse()
    }

    @Test
    internal fun booleanTypeTest() {
        assertThat(BooleanType::class.sqlType()).isEqualTo(BooleanColumnType::class)
        assertThat(BooleanType(true).sqlType()).isEqualTo(BooleanColumnType::class)
        assertThat(BooleanType(true).compareTo(BooleanType(true))).isEqualTo(0)
    }

    @Test
    internal fun longTypeTest() {
        assertThat(LongType::class.sqlType()).isEqualTo(LongColumnType::class)
        assertThat(LongType(3L).sqlType()).isEqualTo(LongColumnType::class)
        assertThat(LongType(3L).compareTo(LongType(3L))).isEqualTo(0)
    }

    @Test
    internal fun doubleTypeTest() {
        assertThat(DoubleType::class.sqlType()).isEqualTo(DoubleColumnType::class)
        assertThat(DoubleType(3.3).sqlType()).isEqualTo(DoubleColumnType::class)
        assertThat(DoubleType(3.3).compareTo(DoubleType(3.3))).isEqualTo(0)
    }

    @Test
    internal fun stringTypeTest() {
        assertThat(StringType::class.sqlType()).isEqualTo(VarCharColumnType::class)
        assertThat(StringType("").sqlType()).isEqualTo(VarCharColumnType::class)
        assertThat(StringType("test").compareTo(StringType("test"))).isEqualTo(0)
    }

    @Test
    internal fun dateTypeTest() {
        assertThat(DateType::class.sqlType()).isEqualTo(DateColumnType::class)
        assertThat(DateType(DateTime()).sqlType()).isEqualTo(DateColumnType::class)
    }

    @Test
    internal fun dateTimeTypeTest() {
        assertThat(DateTimeType::class.sqlType()).isEqualTo(DateColumnType::class)
        assertThat(DateTimeType(DateTime()).sqlType()).isEqualTo(DateColumnType::class)
    }

    @Test
    internal fun textTypeTest() {
        assertThat(TextType::class.sqlType()).isEqualTo(TextColumnType::class)
        assertThat(TextType("").sqlType()).isEqualTo(TextColumnType::class)
        assertThat(TextType("test").compareTo(TextType("test"))).isEqualTo(0)
    }

    @Test
    internal fun listTypesTest() {
        assertThat(BooleanListType(emptyList()).sqlType()).isEqualTo(BooleanColumnType::class)
        assertThat(LongListType(emptyList()).sqlType()).isEqualTo(LongColumnType::class)
        assertThat(DoubleListType(emptyList()).sqlType()).isEqualTo(DoubleColumnType::class)
        assertThat(StringListType(emptyList()).sqlType()).isEqualTo(VarCharColumnType::class)
        assertThat(TextListType(emptyList()).sqlType()).isEqualTo(TextColumnType::class)
        assertThat(DateListType(emptyList()).sqlType()).isEqualTo(DateColumnType::class)
        assertThat((DateTimeListType(emptyList())).sqlType()).isEqualTo(DateColumnType::class)
        assertThat(BooleanListType(emptyList()).toString()).isEqualTo("[]")
        assertThat(AttributeListType(emptyMap()).toString()).isEqualTo("{}")
    }
}
