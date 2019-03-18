package net.pototskiy.apps.lomout.logger

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Core
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Filter.Result
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.filter.AbstractFilter

@Suppress("unused")
@Plugin(
    name = "SkipRepeatedFilter",
    category = Core.CATEGORY_NAME,
    elementType = Filter.ELEMENT_TYPE,
    printObject = true
)
class SkipRepeatedFilter(
    onMatch: Result,
    onMismatch: Result
) : AbstractFilter(onMatch, onMismatch) {

    override fun filter(event: LogEvent?): Result {
        return if (lastMsg == null) {
            updateLast(event)
            return onMatch
        } else {
            val matched = lastMsg == event?.message?.formattedMessage &&
                    lastThread == event?.threadName &&
                    lastLogger == event?.loggerName &&
                    lastLevel == event?.level
            updateLast(event)
            if (matched) {
                onMismatch
            } else {
                onMatch
            }
        }
    }

    private fun updateLast(event: LogEvent?) {
        lastMsg = event?.message?.formattedMessage
        lastThread = event?.threadName
        lastLogger = event?.loggerName
        lastLevel = event?.level
    }

    companion object {
        var lastMsg: String? = null
        var lastThread: String? = null
        var lastLogger: String? = null
        var lastLevel: Level? = null

        @JvmStatic
        @PluginFactory
        fun createSkipRepeatedFilter(
            @PluginAttribute(value = "onMatch", defaultString = "NEUTRAL") onMatch: Result,
            @PluginAttribute(value = "onMismatch", defaultString = "DENY") onMismatch: Result
        ): SkipRepeatedFilter {
            return SkipRepeatedFilter(onMatch, onMismatch)
        }
    }
}
