package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException

data class InputEntityCollection(private val entities: List<InputEntity>) : List<InputEntity> by entities {

    @ConfigDsl
    class Builder {
        private val entities = mutableListOf<InputEntity>()

        @Suppress("unused")
        fun Builder.sourceEntity(name: String, block: InputEntity.Builder.()->Unit = {}) {
            val entity = Config.Builder.definedEntities.findRegistered(name,true)
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
