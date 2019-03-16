package net.pototskiy.apps.magemediation

import net.pototskiy.apps.magemediation.api.ROOT_LOG_NAME
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.filter.LevelRangeFilter
import org.apache.logging.log4j.core.layout.PatternLayout
import java.io.ByteArrayOutputStream

class LogCatcher {
    private lateinit var logOut: ByteArrayOutputStream
    val log: String
        get() = logOut.toString()
    private lateinit var appender: WriterAppender
    private val logger = LogManager.getLogger(ROOT_LOG_NAME) as Logger
    private var layout = PatternLayout.newBuilder()
        .withPattern((logger.appenders["console"]?.layout as? PatternLayout)?.conversionPattern ?: "%level,")
        .build()

    fun startToCatch(minLevel: Level, maxLevel: Level) {
        logOut = ByteArrayOutputStream()
        appender = WriterAppender.createAppender(
            layout,
            LevelRangeFilter.createFilter(minLevel, maxLevel, Filter.Result.NEUTRAL, Filter.Result.DENY),
            logOut.writer(),
            "test-catcher",
            false,
            true
        )
        appender.start()
        logger.addAppender(appender)
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
    }

    fun stopToCatch() {
        logger.removeAppender(appender)
        appender.stop()
        logOut.close()
    }
}
