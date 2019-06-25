package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import org.apache.logging.log4j.LogManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PluginContextTest {
    @Test
    internal fun contextLogTest() {
        val plugin = TestPlugin()
        assertThat(plugin.testLog(ROOT_LOG_NAME)).isEqualTo(true)
        PluginContext.logger = LogManager.getLogger(LOADER_LOG_NAME)
        assertThat(plugin.testLog(LOADER_LOG_NAME)).isEqualTo(true)
        assertThat(plugin.testLog(MEDIATOR_LOG_NAME)).isEqualTo(false)
        PluginContext.logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
        assertThat(plugin.testLog(MEDIATOR_LOG_NAME)).isEqualTo(true)
        assertThat(plugin.testLog(LOADER_LOG_NAME)).isEqualTo(false)
    }

    class TestPlugin : Plugin() {
        fun testLog(name: String): Boolean {
            return logger.name == name
        }
    }
}
