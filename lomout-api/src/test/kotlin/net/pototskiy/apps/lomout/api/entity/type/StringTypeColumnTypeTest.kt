package net.pototskiy.apps.lomout.api.entity.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringTypeColumnTypeTest {
    private val c = StringTypeColumnType().apply { nullable = true }

    @Test
    fun notNullValueToDBTest() {
        assertThat(c.notNullValueToDB(STRING("123"))).isEqualTo("123")
        assertThat(c.notNullValueToDB("123")).isEqualTo("123")
    }

    @Test
    fun valueFromDBTest() {
        assertThat(c.valueFromDB(STRING("123")))
            .isInstanceOf(STRING::class.java)
            .isEqualTo(STRING("123"))
        assertThat(c.valueFromDB("123"))
            .isInstanceOf(STRING::class.java)
            .isEqualTo(STRING("123"))
    }

    @Test
    fun valueToDBTest() {
        assertThat(c.valueToDB(null)).isNull()
        assertThat(c.valueToDB(STRING("123"))).isEqualTo("123")
        assertThat(c.valueToDB("123")).isEqualTo("123")
    }

    @Test
    fun nonNullValueToStringTest() {
        assertThat(c.nonNullValueToString(STRING("123"))).isEqualTo("'123'")
        assertThat(c.nonNullValueToString("123")).isEqualTo("'123'")
    }

    @Test
    fun valueToStringTest() {
        assertThat(c.valueToString(null)).isEqualTo("NULL")
        assertThat(c.valueToString(STRING("123"))).isEqualTo("'123'")
        assertThat(c.valueToString("123")).isEqualTo("'123'")
    }
}