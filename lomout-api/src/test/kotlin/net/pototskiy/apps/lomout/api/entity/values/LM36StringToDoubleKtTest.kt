package net.pototskiy.apps.lomout.api.entity.values

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.text.ParseException
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
internal class LM36StringToDoubleKtTest {

    @org.junit.jupiter.api.Test
    fun stringToDouble() {
        assertThat("1.2".stringToDouble(Locale("en_US"), false)).isEqualTo(1.2)
        assertThatThrownBy {
            "1.2".stringToDouble(Locale("ru"), false)
        }.isInstanceOf(ParseException::class.java)
        assertThat("1,2".stringToDouble(Locale("ru"),false)).isEqualTo(1.2)
        assertThat("2${160.toChar()}1,2".stringToDouble(Locale("ru"),true)).isEqualTo(21.2)
        assertThatThrownBy {
            "2${160.toChar()}1,2".stringToDouble(Locale("ru"),false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String contains extra characters.")
        assertThatThrownBy {
            "abc".stringToDouble(Locale("ru"), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String cannot be parsed to double.")
    }
}
