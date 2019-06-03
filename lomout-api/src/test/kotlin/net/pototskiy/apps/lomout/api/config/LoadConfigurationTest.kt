package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.LogCatcher
import net.pototskiy.apps.lomout.api.NoExitSecurityManager
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
    private val errorFreeConf = "${System.getenv("TEST_DATA_DIR")}/error-free.conf.kts"
    private val emptyConf = "${System.getenv("TEST_DATA_DIR")}/empty-config.conf.kts"
    private val importErrorConf = "${System.getenv("TEST_DATA_DIR")}/import-error.conf.kts"
    @Suppress("SpellCheckingInspection", "GraziInspection")
    private val depenedsOnErrorConf = "${System.getenv("TEST_DATA_DIR")}/dependson-error.conf.kts"

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
    internal fun loadEmptyConfigTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(emptyConf)).config
        } catch (e: Exception) {
            assertThat(true).isTrue()
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        @Suppress("RegExpRedundantEscape", "GraziInspection")
        assertThat(log.contains(Regex("^\\[ERROR\\] .* - Script cannot be evaluated$"))).isTrue()
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
        assertThat(log).contains("Source file 'file-2' is not defined. (evaluate-error.conf.kts:20)")
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigImportErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(importErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isTrue()
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(
            log.contains(
                Regex(
                    ".*ERROR.*- Source file or directory not found.*absent-file.plugin.conf.kts"
                )
            )
        ).isTrue()
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadConfigDependsOnErrorTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            ConfigurationBuilderFromDSL(File(depenedsOnErrorConf)).config
        } catch (e: Exception) {
            assertThat(true).isTrue()
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(
            log.contains(
                Regex(
                    ".*ERROR.*- Unable to resolve dependency.*absent-file.plugin.conf.kts"
                )
            )
        ).isTrue()
    }

    @Suppress("TooGenericExceptionCaught")
    @Test
    internal fun loadErrorFreeConfigurationTest() {
        logCatcher.startToCatch(Level.OFF, Level.ERROR)
        try {
            val v = ConfigurationBuilderFromDSL(File(errorFreeConf))
            assertThat(v.config).isNotNull
            assertThat(v.config).isNotNull
        } catch (e: Exception) {
            assertThat(true).isTrue()
        }
        val log = logCatcher.log
        logCatcher.stopToCatch()
        assertThat(log.contains("ERROR")).isFalse()
    }
}
