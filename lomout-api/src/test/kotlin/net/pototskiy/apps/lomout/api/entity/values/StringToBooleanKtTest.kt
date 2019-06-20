package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.createLocale
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.text.ParseException

@Execution(ExecutionMode.CONCURRENT)
internal class StringToBooleanKtTest {
    @Test
    internal fun stringToBooleanTest() {
        assertThat("true".stringToBoolean("en_US".createLocale())).isEqualTo(true)
        assertThat("false".stringToBoolean("en_US".createLocale())).isEqualTo(false)
        assertThat("истина".stringToBoolean("ru_RU".createLocale())).isEqualTo(true)
        assertThat("ложь".stringToBoolean("ru_RU".createLocale())).isEqualTo(false)
        assertThatThrownBy { "true".stringToBoolean("ru_RU".createLocale()) }
            .isInstanceOf(ParseException::class.java)
            .hasMessageContaining("Value 'true' cannot be converted to boolean.")
    }
}
