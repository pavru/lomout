package net.pototskiy.apps.lomout.api.entity.values

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.text.ParseException
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
internal class LM36StringToLongKtTest {

    @org.junit.jupiter.api.Test
    fun stringToLong() {
        assertThat("12".stringToLong(Locale("en_US"), false)).isEqualTo(12L)
        assertThat("12".stringToLong(Locale("ru_RU"), false)).isEqualTo(12L)
        assertThatThrownBy {
            "1.2".stringToLong(Locale("en_US"), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String contains extra characters.")
        assertThatThrownBy {
            "1,2".stringToLong(Locale("en_US"), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String contains extra characters.")
        assertThat("1,2".stringToLong(Locale("en_US"), true)).isEqualTo(12L)
        assertThat("1${160.toChar()}2".stringToLong(Locale("ru"), true)).isEqualTo(12L)
        assertThatThrownBy {
            "abc".stringToLong(Locale.US, true)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String cannot be parsed to long.")
    }
}
