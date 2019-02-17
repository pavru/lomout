package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity

data class InputEntityCollection(private val entities: List<InputEntity>) : List<InputEntity> by entities {
    fun mapEntityData(entity: PersistentSourceEntity): Map<Attribute, Any?> {
        val inputEntity = find { it.entity.name == entity.entityType }
            ?: throw ConfigException("Input entity<${entity.entityType}> has not been defined")
        return inputEntity.mapAttributes(entity)
    }

    @ConfigDsl
    class Builder {
        private val entities = mutableListOf<InputEntity>()

        @Suppress("unused")
        fun Builder.entity(name: String, block: InputEntity.Builder.() -> Unit = {}) {
            val entity = Config.Builder.definedEntities.findRegistered(name, true)
                ?: throw ConfigException("Entity<$name> has not been defined yet")
            entities.add(InputEntity.Builder(entity).apply(block).build())
        }

        fun build(): InputEntityCollection {
            if (entities.isEmpty()) {
                throw ConfigException("At least one input entity must be defined")
            }
            return InputEntityCollection(entities)
        }
    }
}
