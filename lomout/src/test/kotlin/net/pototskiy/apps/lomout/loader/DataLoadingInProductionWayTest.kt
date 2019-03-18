package net.pototskiy.apps.lomout.loader

import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.filter.LevelRangeFilter
import org.apache.logging.log4j.core.layout.PatternLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.ByteArrayOutputStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Load data in production way")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Execution(ExecutionMode.SAME_THREAD)
internal class DataLoadingInProductionWayTest {

    private lateinit var config: Config
    private val logOut = ByteArrayOutputStream()
    private lateinit var appender: WriterAppender

    @BeforeAll
    internal fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        val layout = PatternLayout.newBuilder()
            .withPattern("%level,")
            .build()
        appender = WriterAppender.createAppender(
            layout,
            LevelRangeFilter.createFilter(Level.OFF, Level.ERROR, Filter.Result.NEUTRAL, Filter.Result.DENY),
            logOut.writer(),
            "test-catcher",
            false,
            true
        )
        appender.start()
        logger.addAppender(appender)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        val util = LoadingDataTestPrepare()
        println("config file: ${System.getenv("PRODUCTION_CONFIG")}")
        config = util.loadConfiguration(System.getenv("PRODUCTION_CONFIG"))
        util.initDataBase(config.entityTypeManager)
        PluginContext.config = config
        PluginContext.entityTypeManager = config.entityTypeManager
    }

    @ObsoleteCoroutinesApi
    @Test
    @DisplayName("Load data according production config")
    internal fun loadDataTest() {
        DataLoader.load(config)
        assertThat(logOut.toString()).isEmpty()
    }

    @AfterAll
    internal fun finishAll() {
        val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
        logger.removeAppender(appender)
        appender.stop()
    }
}
