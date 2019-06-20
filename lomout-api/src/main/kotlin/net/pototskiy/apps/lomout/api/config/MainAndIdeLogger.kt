package net.pototskiy.apps.lomout.api.config

import com.intellij.openapi.diagnostic.Logger
import net.pototskiy.apps.lomout.api.CONFIG_LOG_NAME
import org.apache.logging.log4j.LogManager
import java.io.Serializable

/**
 * Logger for script tools, log messages into the application or ide log
 *
 * @property mainLogger Logger?
 * @property ideLogger Logger?
 */
class MainAndIdeLogger : Serializable {
    @Suppress("TooGenericExceptionCaught")
    private val mainLogger = try {
        LogManager.getLogger(CONFIG_LOG_NAME)
    } catch (e: Throwable) {
        null
    }
    @Suppress("TooGenericExceptionCaught")
    private val ideLogger = try {
        Logger.getInstance("lomout")
    } catch (e: Throwable) {
        null
    }

    /**
     * Info level message
     *
     * @param message String
     * @param throwable Throwable
     */
    fun info(message: String, throwable: Throwable? = null) {
        mainLogger?.info(message, throwable)
        ideLogger?.info(message, throwable)
    }

    /**
     * Warn level log message
     *
     * @param message String
     * @param throwable Throwable
     */
    fun warn(message: String, throwable: Throwable? = null) {
        mainLogger?.warn(message, throwable)
        ideLogger?.warn(message, throwable)
    }

    /**
     * Error level log message
     *
     * @param message String
     * @param throwable Throwable
     */
    fun error(message: String, throwable: Throwable? = null) {
        mainLogger?.error(message, throwable)
        ideLogger?.trace(message)
        ideLogger?.trace(throwable)
    }

    /**
     * Trace level log message
     *
     * @param message String
     * @param throwable Throwable
     */
    fun trace(message: String, throwable: Throwable? = null) {
        mainLogger?.trace(message, throwable)
        ideLogger?.trace(message)
        throwable?.let { ideLogger?.trace(throwable) }
    }

    /**
     * Debug level log message
     *
     * @param message The message
     * @param throwable The cause
     */
    fun debug(message: String, throwable: Throwable? = null) {
        mainLogger?.debug(message, throwable)
        ideLogger?.debug(message, throwable)
    }
}
