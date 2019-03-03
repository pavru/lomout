package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.config.loader.SourceFileDefinition
import net.pototskiy.apps.magemediation.api.entity.EType
import java.util.Stack

open class ConfigBuildHelper {
    private val scopeStack = Stack<String>().apply { push("root") }
    fun pushScope(name: String) = scopeStack.push(name)!!
    fun popScope(): String = scopeStack.pop()
    fun currentScope(): String = scopeStack.peek()

    private val definedEntities = ConfigObjectRegistrar<EType>()
    val definedSourceFiles = ConfigObjectRegistrar<SourceFileDefinition>()

    fun initConfigBuilder() {
        scopeStack.clear()
        scopeStack.push("root")
        definedEntities.clear()
        definedSourceFiles.clear()
    }

    inner class ConfigObjectRegistrar<T : NamedObject> {
        private var register = mutableMapOf<String, MutableList<T>>()

        fun clear() = register.clear()

        fun register(entity: T) {
            val scope = currentScope()
            if (register[scope]?.any { it.name == entity.name } == true)
                throw ConfigException("Object is already defined in scope<$scope>")
            register.getOrPut(scope) { mutableListOf() }.add(entity)
        }

        fun findRegistered(name: String, globalSearch: Boolean = false): T? {
            val obj: T? = scopeStack.reversed().mapNotNull { scope ->
                register.getOrPut(scope) { mutableListOf() }.find { it.name == name }
            }.firstOrNull()
            return obj
                ?: (if (globalSearch) register.map { it.value }.flatten().find { it.name == name } else null)
        }
    }
}
