package net.pototskiy.apps.lomout.api.entity.type

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TextTypeColumnTypeTest {
    private val c = TextTypeColumnType().apply { nullable = true }

    @Test
    fun notNullValueToDB() {
        assertThat(c.notNullValueToDB(TEXT("123"))).isEqualTo("123")
        assertThat(c.notNullValueToDB("123")).isEqualTo("123")
    }

    @Test
    fun valueFromDB() {
        assertThat(c.valueFromDB(TEXT("123")))
            .isInstanceOf(TEXT::class.java)
            .isEqualTo(TEXT("123"))
        assertThat(c.valueFromDB("123"))
            .isInstanceOf(TEXT::class.java)
            .isEqualTo(TEXT("123"))
    }

    @Test
    fun valueToDB() {
        assertThat(c.valueToDB(null)).isNull()
        assertThat(c.valueToDB(TEXT("123"))).isEqualTo("123")
        assertThat(c.valueToDB("123")).isEqualTo("123")
    }

    @Test
    fun nonNullValueToString() {
        assertThat(c.nonNullValueToString(TEXT("123"))).isEqualTo("'123'")
        assertThat(c.nonNullValueToString("123")).isEqualTo("'123'")
    }

    @Test
    fun valueToString() {
        assertThat(c.valueToString(null)).isEqualTo("NULL")
        assertThat(c.valueToString(TEXT("123"))).isEqualTo("'123'")
        assertThat(c.valueToString("123")).isEqualTo("'123'")
    }
}