package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.source.workbook.SourceException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class CheckAndRemoveQuoteKtTest {
    @Test
    internal fun testCorrectQuoted() {
        assertThat(listOf(" '1234'  ", "'2345''").checkAndRemoveQuote("'"))
            .isEqualTo(listOf("1234", "2345'"))
    }

    @Test
    internal fun testIncorrectEndQuoted() {
        assertThatThrownBy { (listOf(" '1234'  ", "'2345").checkAndRemoveQuote("'")) }
            .isInstanceOf(SourceException::class.java)
    }

    @Test
    internal fun testIncorrectStartQuoted() {
        assertThatThrownBy { (listOf(" '1234'  ", " 2345'").checkAndRemoveQuote("'")) }
            .isInstanceOf(SourceException::class.java)
    }

    @Test
    internal fun testUnQuoted() {
        assertThat(listOf(" '1234'  ", "'2345''").checkAndRemoveQuote(null))
            .isEqualTo(listOf(" '1234'  ", "'2345''"))
    }
}
