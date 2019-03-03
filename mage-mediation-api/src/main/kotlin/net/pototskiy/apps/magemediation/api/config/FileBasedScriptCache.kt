package net.pototskiy.apps.magemediation.api.config

import org.jetbrains.kotlin.daemon.common.toHexString
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.security.MessageDigest
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.jvmhost.CompiledJvmScriptsCache
import kotlin.script.experimental.jvmhost.impl.KJvmCompiledScript

// TODO: 07.02.2019 remove suppress after bug with serialization will be resolved
@Suppress("unused")
class FileBasedScriptCache(private val baseDir: File) : CompiledJvmScriptsCache {

    private fun uniqueHash(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): String {
        val digestWrapper = MessageDigest.getInstance("MD5")
        digestWrapper.update(script.text.toByteArray())
        scriptCompilationConfiguration.entries().sortedBy { it.key.name }.forEach {
            digestWrapper.update(it.key.name.toByteArray())
            digestWrapper.update(it.value.toString().toByteArray())
        }
        return digestWrapper.digest().toHexString()
    }

    override fun get(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*>? {
        val file = File(baseDir, uniqueHash(script, scriptCompilationConfiguration))
        return if (!file.exists()) null else file.readCompiledScript(scriptCompilationConfiguration)
    }

    override fun store(
        compiledScript: CompiledScript<*>,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        if (!baseDir.exists()) baseDir.mkdirs()
        val file = File(baseDir, uniqueHash(script, scriptCompilationConfiguration))
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
