package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.loader.SourceFileDefinition
import net.pototskiy.apps.lomout.api.unknownPlace
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import java.util.*

/**
 * Helper for configuration building
 *
 * @property typeManager The entity type manager
 * @property scopeStack The scope stack
 * @property definedEntities The registrar for define entities
 * @property definedSourceFiles The registrar for defined source fields
 * @constructor
 */
open class ConfigBuildHelper(val typeManager: EntityTypeManagerImpl) {

    private val scopeStack = Stack<String>().apply { push("root") }
    /**
     * Push scope to stack
     *
     * @param name The scope name
     * @return String
     */
    fun pushScope(name: String) = scopeStack.push(name)!!

    /**
     * Pop scope from a stack
     *
     * @return String
     */
    fun popScope(): String = scopeStack.pop()

    /**
     * Get current scope
     *
     * @return String
     */
    fun currentScope(): String = scopeStack.peek()

    private val definedEntities = ConfigObjectRegistrar<EntityType>()
    val definedSourceFiles = ConfigObjectRegistrar<SourceFileDefinition>()

    init {
        scopeStack.clear()
        scopeStack.push("root")
        definedEntities.clear()
        definedSourceFiles.clear()
    }

    /**
     * Config object registrar
     *
     * @param T The registrar object type
     * @property register MutableMap<String, MutableList<T>>
     */
    inner class ConfigObjectRegistrar<T : NamedObject> {
        private var register = mutableMapOf<String, MutableList<T>>()

        /**
         * Clear registered objects
         */
        fun clear() = register.clear()

        /**
         * Register object
         *
         * @param entity T
         */
        fun register(entity: T) {
            val scope = currentScope()
            if (register[scope]?.any { it.name == entity.name } == true)
                throw AppConfigException(unknownPlace(), "The scope '$scope' already has the object.")
            register.getOrPut(scope) { mutableListOf() }.add(entity)
        }

        /**
         * Find registered object by name.
         *
         * @param name The object name
         * @param globalSearch true — search in all scopes, false — search only in current scope
         * @return T?
         */
        fun findRegistered(name: String, globalSearch: Boolean = false): T? {
            val obj: T? = scopeStack.reversed().mapNotNull { scope ->
                register.getOrPut(scope) { mutableListOf() }.find { it.name == name }
            }.firstOrNull()
            return obj
                ?: (if (globalSearch) register.map { it.value }.flatten().find { it.name == name } else null)
        }
    }
}
