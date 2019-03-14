package net.pototskiy.apps.magemediation.api.log

import org.apache.logging.log4j.Level
import kotlin.script.experimental.api.ScriptDiagnostic

private val severityToLevel = mapOf(
    ScriptDiagnostic.Severity.FATAL to Level.FATAL,
    ScriptDiagnostic.Severity.ERROR to Level.ERROR,
    ScriptDiagnostic.Severity.WARNING to Level.WARN,
    ScriptDiagnostic.Severity.INFO to Level.INFO,
    ScriptDiagnostic.Severity.DEBUG to Level.DEBUG
)

fun ScriptDiagnostic.Severity.toLogLevel(): Level = severityToLevel.getValue(this)
