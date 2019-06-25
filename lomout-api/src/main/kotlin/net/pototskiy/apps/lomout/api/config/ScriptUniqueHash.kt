package net.pototskiy.apps.lomout.api.config

import org.jetbrains.kotlin.daemon.common.toHexString
import java.io.File
import java.security.MessageDigest
import kotlin.script.experimental.api.ExternalSourceCode
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode

/**
 * Script hash creator, including script imports
 *
 * @property script The script source code
 * @property compileConfiguration ScriptCompilationConfiguration
 * @constructor
 */
class ScriptUniqueHash(
    private val script: SourceCode,
    private val compileConfiguration: ScriptCompilationConfiguration
) {
    /**
     * Create script SHA-1 hash code
     *
     * @return String?
     */
    fun hash(): String? {
        if (script !is ExternalSourceCode) {
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

    private fun updateHashWithTextImports(script: ExternalSourceCode, digestWrapper: MessageDigest) {
        val scriptDir = File(script.externalLocation.toURI()).parentFile
        digestWrapper.update(script.text.toByteArray())
        @Suppress("GraziInspection")
        val importPattern = Regex("^@file:Import\\s*\\(\\s*\"([^\"]*)\"\\s*\\)")
        script.text.lines()
            .filter { it.matches(importPattern) }
            .forEach {
                val file = importPattern.matchEntire(it)?.groups?.get(1)?.value
                if (file != null && File(scriptDir, file).exists() && File(scriptDir, file).exists()) {
                    updateHashWithTextImports(SerializableFileScriptSource(File(scriptDir, file)), digestWrapper)
                }
            }
    }
}
