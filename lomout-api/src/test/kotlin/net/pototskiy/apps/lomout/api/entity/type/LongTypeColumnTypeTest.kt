package net.pototskiy.apps.lomout.api.entity.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LongTypeColumnTypeTest {
    private val c = LongTypeColumnType(). apply { nullable = true }

    @Test
    fun notNullValueToDBTest() {
        assertThat(c.notNullValueToDB(LONG(123L))).isEqualTo(123L)
        assertThat(c.notNullValueToDB(123L)).isEqualTo(123L)
    }

    @Test
    fun valueFromDBTest() {
        assertThat(c.valueFromDB(LONG(123L)))
            .isInstanceOf(LONG::class.java)
            .isEqualTo(LONG(123L))
        assertThat((c.valueFromDB(123L)))
            .isInstanceOf(LONG::class.java)
            .isEqualTo(LONG(123L))
    }

    @Test
    fun valueToDBTest() {
        assertThat(c.valueToDB(null)).isNull()
        assertThat(c.valueToDB(LONG(123L))).isEqualTo(123L)
        assertThat(c.valueToDB(123L)).isEqualTo(123L)
    }

    @Test
    fun nonNullValueToStringTest() {
        assertThat(c.nonNullValueToString(LONG(123L))).isEqualTo("123")
        assertThat(c.nonNullValueToString(123L)).isEqualTo("123")
    }

    @Test
    fun valueToStringTest() {
        assertThat(c.valueToString(null)).isEqualTo("NULL")
        assertThat(c.valueToString(LONG(123L))).isEqualTo("123")
        assertThat(c.valueToString(123L)).isEqualTo("123")
    }
}