package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.loader.SourceFileDefinition
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import java.util.*

open class ConfigBuildHelper(val typeManager: EntityTypeManager) {

    private val scopeStack = Stack<String>().apply { push("root") }
    fun pushScope(name: String) = scopeStack.push(name)!!
    fun popScope(): String = scopeStack.pop()
    fun currentScope(): String = scopeStack.peek()

    private val definedEntities = ConfigObjectRegistrar<EntityType>()
    val definedSourceFiles = ConfigObjectRegistrar<SourceFileDefinition>()

    init {
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
                throw AppConfigException("Object is already defined in scope<$scope>")
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
