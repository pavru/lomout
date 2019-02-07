package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

class EntityCollection(private val entities: List<Entity>) : List<Entity> by entities {
    @ConfigDsl
    class Builder {
        private val entities = mutableListOf<Entity>()

        @Suppress("unused")
        fun Builder.entity(name: String, open: Boolean = false, block: Entity.Builder.() -> Unit) =
            entities.add(Entity.Builder(name, open).apply(block).build())

        fun build(): EntityCollection = EntityCollection(entities)
    }
}
