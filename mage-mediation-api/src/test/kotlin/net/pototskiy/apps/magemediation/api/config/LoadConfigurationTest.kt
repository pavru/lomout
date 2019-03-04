package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.LogCatcher
import net.pototskiy.apps.magemediation.api.NoExitSecurityManager
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@DisplayName("Just load configuration file")
internal class LoadConfigurationTest {
    private val logCatcher = LogCatcher()
    private val compileErrorConf = "${System.getenv("TEST_DATA_DIR")}/compile-error.conf.kts"
    private val evaluateErrorConf = "${System.getenv("TEST_DATA_DIR")}/evaluate-error.conf.kts"

    @BeforeEach
    internal fun setUp() {
        System.setSecurityManager(NoExitSecurityManager())
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigWithCompileErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(compileErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isTrue()
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log).contains("Expecting '\"' (compile-error.conf.kts:3)")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigWithEvaluateErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(evaluateErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isTrue()
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log).contains("Source file<id:file-2> is not defined (evaluate-error.conf.kts:20)")
    }
}
