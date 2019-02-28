package net.pototskiy.apps.magemediation.loader

import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.magemediation.api.ROOT_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.filter.LevelRangeFilter
import org.apache.logging.log4j.core.layout.PatternLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.io.ByteArrayOutputStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Load data in production way")
class DataLoadingInProductionWayTest {

    private lateinit var config: Config
    private val logOut = ByteArrayOutputStream()
    private lateinit var appender: WriterAppender


    @BeforeAll
    fun initAll() {
        System.setSecurityManager(NoExitSecurityManager())
        EntityTypeManager.cleanEntityTypeConfiguration()
        Config.Builder.initConfigBuilder()
        // TODO: 23.02.2019 remove after test
        //EntityClass.initEntityCLassRegistrar()
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
        println("congig file: ${System.getenv("PRODUCTION_CONFIG")}")
        config = util.loadConfiguration(System.getenv("PRODUCTION_CONFIG"))
        util.initDataBase()
    }

    @ObsoleteCoroutinesApi
    @Test
    @DisplayName("Load data according production config")
    fun loadDataTest() {
        DataLoader.load(config)
        assertThat(logOut.toString()).isEmpty()
    }

    @AfterAll
    fun finishAll() {
        val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
        logger.removeAppender(appender)
        appender.stop()
    }
}
