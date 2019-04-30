package net.pototskiy.apps.lomout.api.config

import org.jetbrains.kotlin.daemon.common.toHexString
import java.io.File
import java.security.MessageDigest
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.FileScriptSource

class ScriptUniqueHash(
    private val script: SourceCode,
    private val compileConfiguration: ScriptCompilationConfiguration
) {
    fun hash(): String? {
        if (script !is FileScriptSource) {
            return null
        }
        val digestWrapper = MessageDigest.getInstance("SHA-1")
        updateHashWithTextImports(script, digestWrapper)
        compileConfiguration.entries().sortedBy { it.key.name }.forEach {
            digestWrapper.update(it.key.name.toByteArray())
            digestWrapper.update(it.value.toString().toByteArray())
        }
        return digestWrapper.digest().toHexString()
    }

    private fun updateHashWithTextImports(script: FileScriptSource, digestWrapper: MessageDigest) {
        val scriptDir = script.file.parentFile
        digestWrapper.update(script.text.toByteArray())
        val importPattern = Regex("^@file:Import\\s*\\(\\s*\"([^\"]*)\"\\s*\\)")
        script.text.lines()
            .filter { it.matches(importPattern) }
            .forEach {
                val file = importPattern.matchEntire(it)?.groups?.get(1)?.value
                if (file != null && File(scriptDir, file).exists() && File(scriptDir, file).exists()) {
                    updateHashWithTextImports(FileScriptSource(File(scriptDir, file)), digestWrapper)
                }
            }
    }
}
