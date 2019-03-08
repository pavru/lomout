package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.database.DatabaseException
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
        assertThat(StringValue("").sqlType()).isEqualTo(VarCharColumnType::class)
        assertThatThrownBy { StringValue("", true).sqlType() }
            .isInstanceOf(DatabaseException::class.java)
        assertThatThrownBy { AttributeListValue(emptyMap()).sqlType() }
            .isInstanceOf(DatabaseException::class.java)
        assertThatThrownBy { AttributeListValue(emptyMap(), false).sqlType() }
            .isInstanceOf(DatabaseException::class.java)
        assertThat(StringType::class.sqlType()).isEqualTo(VarCharColumnType::class)
        assertThatThrownBy { AttributeListType::class.sqlType() }.isInstanceOf(DatabaseException::class.java)
    }

    @Test
    internal fun isSingleTest() {
        assertThat(StringType::class.isSingle()).isTrue()
        assertThat(StringListType::class.isSingle()).isFalse()
        assertThat(AttributeListType::class.isSingle()).isFalse()
        assertThat(StringValue("").isSingle()).isTrue()
        assertThat(StringListValue(emptyList()).isSingle()).isFalse()
        assertThat(AttributeListValue(emptyMap()).isSingle()).isFalse()
    }

    @Test
    internal fun isListTest() {
        assertThat(StringListType::class.isList()).isTrue()
        assertThat(StringType::class.isList()).isFalse()
        assertThat(StringListValue(emptyList()).isList()).isTrue()
        assertThat(StringValue("").isList()).isFalse()
    }

    @Test
    internal fun isMapTest() {
        assertThat(AttributeListType::class.isMap()).isTrue()
        assertThat(StringType::class.isMap()).isFalse()
        assertThat(AttributeListValue(emptyMap()).isMap()).isTrue()
        assertThat(StringValue("").isMap()).isFalse()
    }

    @Test
    internal fun isTypeOfTest() {
        assertThat(StringType::class.isTypeOf<StringType>()).isTrue()
        assertThat(StringValue("").isTypeOf<StringType>()).isTrue()
        assertThat(StringType::class.isTypeOf<LongType>()).isFalse()
        assertThat(StringValue("").isTypeOf<LongType>()).isFalse()
    }

    @Test
    internal fun booleanTypeTest() {
        assertThat(BooleanType::class.sqlType()).isEqualTo(BooleanColumnType::class)
        assertThat(BooleanValue(true).sqlType()).isEqualTo(BooleanColumnType::class)
        assertThat(BooleanValue(true).compareTo(BooleanValue(true))).isEqualTo(0)
    }

    @Test
    internal fun longTypeTest() {
        assertThat(LongType::class.sqlType()).isEqualTo(LongColumnType::class)
        assertThat(LongValue(3L).sqlType()).isEqualTo(LongColumnType::class)
        assertThat(LongValue(3L).compareTo(LongValue(3L))).isEqualTo(0)
    }

    @Test
    internal fun doubleTypeTest() {
        assertThat(DoubleType::class.sqlType()).isEqualTo(DoubleColumnType::class)
        assertThat(DoubleValue(3.3).sqlType()).isEqualTo(DoubleColumnType::class)
        assertThat(DoubleValue(3.3).compareTo(DoubleValue(3.3))).isEqualTo(0)
    }

    @Test
    internal fun stringTypeTest() {
        assertThat(StringType::class.sqlType()).isEqualTo(VarCharColumnType::class)
        assertThat(StringValue("").sqlType()).isEqualTo(VarCharColumnType::class)
        assertThat(StringValue("test").compareTo(StringValue("test"))).isEqualTo(0)
    }

    @Test
    internal fun dateTypeTest() {
        assertThat(DateType::class.sqlType()).isEqualTo(DateColumnType::class)
        assertThat(DateValue(DateTime()).sqlType()).isEqualTo(DateColumnType::class)
    }

    @Test
    internal fun dateTimeTypeTest() {
        assertThat(DateTimeType::class.sqlType()).isEqualTo(DateColumnType::class)
        assertThat(DateTimeValue(DateTime()).sqlType()).isEqualTo(DateColumnType::class)
    }

    @Test
    internal fun textTypeTest() {
        assertThat(TextType::class.sqlType()).isEqualTo(TextColumnType::class)
        assertThat(TextValue("").sqlType()).isEqualTo(TextColumnType::class)
        assertThat(TextValue("test").compareTo(TextValue("test"))).isEqualTo(0)
    }

    @Test
    internal fun listTypesTest() {
        assertThat(BooleanListValue(emptyList()).sqlType()).isEqualTo(BooleanColumnType::class)
        assertThat(LongListValue(emptyList()).sqlType()).isEqualTo(LongColumnType::class)
        assertThat(DoubleListValue(emptyList()).sqlType()).isEqualTo(DoubleColumnType::class)
        assertThat(StringListValue(emptyList()).sqlType()).isEqualTo(VarCharColumnType::class)
        assertThat(TextListValue(emptyList()).sqlType()).isEqualTo(TextColumnType::class)
        assertThat(DateListValue(emptyList()).sqlType()).isEqualTo(DateColumnType::class)
        assertThat((DateTimeListValue(emptyList())).sqlType()).isEqualTo(DateColumnType::class)
        assertThat(BooleanListValue(emptyList()).toString()).isEqualTo("[]")
        assertThat(AttributeListValue(emptyMap()).toString()).isEqualTo("{}")
    }
}
