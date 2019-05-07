package net.pototskiy.apps.lomout.api.config

import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class MainAndIdeLoggerTest {

    @org.junit.jupiter.api.Test
    fun info() {
        val logger = MainAndIdeLogger()
        logger.info("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun warn() {
        val logger = MainAndIdeLogger()
        logger.warn("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun error() {
        val logger = MainAndIdeLogger()
        logger.error("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun trace() {
        val logger = MainAndIdeLogger()
        logger.trace("Test message", Exception("Test exception"))
        assert(true)
    }

    @org.junit.jupiter.api.Test
    fun debug() {
        val logger = MainAndIdeLogger()
        logger.debug("Test message", Exception("Test exception"))
        assert(true)
    }
}
