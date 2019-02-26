package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class InputEntityCollection(private val entities: List<InputEntity>) : List<InputEntity> by entities {

    @ConfigDsl
    class Builder {
        private val entities = mutableListOf<InputEntity>()

        @Suppress("unused")
        fun Builder.entity(name: String, block: InputEntity.Builder.() -> Unit = {}) {
            val entity = EntityTypeManager.getEntityType(name)
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
