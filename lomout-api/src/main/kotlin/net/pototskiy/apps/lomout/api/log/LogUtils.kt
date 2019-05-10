package net.pototskiy.apps.lomout.api.log

import org.apache.logging.log4j.Level
import kotlin.script.experimental.api.ScriptDiagnostic

/**
 * Map config script compilation and evaluation message severity to log level
 */
private val severityToLevel = mapOf(
    ScriptDiagnostic.Severity.FATAL to Level.FATAL,
    ScriptDiagnostic.Severity.ERROR to Level.ERROR,
    ScriptDiagnostic.Severity.WARNING to Level.WARN,
    ScriptDiagnostic.Severity.INFO to Level.INFO,
    ScriptDiagnostic.Severity.DEBUG to Level.DEBUG
)

/**
 * Convert config script compilation and evaluation message severity to log level
 *
 * @receiver ScriptDiagnostic.Severity The message severity
 * @return Level The related log level
 */
fun ScriptDiagnostic.Severity.toLogLevel(): Level = severityToLevel.getValue(this)
