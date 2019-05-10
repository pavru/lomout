package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.Generated
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.jvmhost.CompiledJvmScriptsCache
import kotlin.script.experimental.jvmhost.impl.KJvmCompiledScript

/**
 * File base compiled script cache
 *
 * @property baseDir The cache directory
 * @property doNotUseCache True - do not use cache, false - cache script
 * @constructor
 */
@Suppress("unused")
@Generated
class FileBasedScriptCache(
    private val baseDir: File,
    private val doNotUseCache: Boolean
) : CompiledJvmScriptsCache {

    /**
     * Try get script from cache
     *
     * @param script The script source code
     * @param scriptCompilationConfiguration ScriptCompilationConfiguration
     * @return CompiledScript<*>?
     */
    @Suppress("ReturnCount")
    override fun get(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*>? {
        if (doNotUseCache) return null
        val scriptHash = ScriptUniqueHash(script, scriptCompilationConfiguration).hash()
            ?: return null
        val file = File(baseDir, scriptHash)
        return if (!file.exists()) null else file.readCompiledScript(scriptCompilationConfiguration)
    }

    /**
     * Store compiled script in cache
     *
     * @param compiledScript The compiled script
     * @param script SourceCode The script source code
     * @param scriptCompilationConfiguration ScriptCompilationConfiguration
     */
    override fun store(
        compiledScript: CompiledScript<*>,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        if (doNotUseCache) return
        if (!baseDir.exists()) baseDir.mkdirs()
        val scriptHash = ScriptUniqueHash(script, scriptCompilationConfiguration).hash()
        val file = File(baseDir, scriptHash)
        file.outputStream().use { fs ->
            ObjectOutputStream(fs).use { os ->
                os.writeObject(compiledScript)
            }
        }
    }

    private fun File.readCompiledScript(
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*> {
        return inputStream().use { fs ->
            ObjectInputStream(fs).use { os ->
                (os.readObject() as KJvmCompiledScript<*>).apply {
                    setCompilationConfiguration(scriptCompilationConfiguration)
                }
            }
        }
    }
}
