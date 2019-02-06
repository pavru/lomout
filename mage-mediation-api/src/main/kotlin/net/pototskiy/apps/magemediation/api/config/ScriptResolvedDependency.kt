package net.pototskiy.apps.magemediation.api.config

import java.io.File
import kotlin.script.dependencies.KotlinScriptExternalDependencies

class ScriptResolvedDependency(
    previousDependencies: KotlinScriptExternalDependencies?,
    addDeps: List<File>,
    addImports: List<String>,
    addScripts: List<File>
) : KotlinScriptExternalDependencies {
    override val classpath = previousDependencies?.classpath?.let {
        it.plus(addDeps.minus(it))
    } ?: addDeps
    override val imports = previousDependencies?.imports?.let {
        it.plus(addImports.minus(it))
    } ?: addImports
    override val scripts = previousDependencies?.scripts?.let {
        it.plus(addScripts.minus(it))
    } ?: addScripts
}
