package net.pototskiy.apps.lomout.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
internal class CreateLocaleKtTest {
    @Test
    internal fun tryToCreateIncorrectLocaleTest() {
        @Suppress("SpellCheckingInspection", "GraziInspection")
        assertThat("xxxx".createLocale()).isEqualTo(Locale.getDefault())
    }
}
