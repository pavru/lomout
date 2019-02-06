package net.pototskiy.apps.magemediation.api.config.data

class EntityCollection(private val entities: List<Entity>) : List<Entity> by entities {
    class Builder() {
        private val entities = mutableListOf<Entity>()

        @Suppress("unused")
        fun Builder.entity(name: String, open: Boolean = false, block: Entity.Builder.() -> Unit) =
            entities.add(Entity.Builder(name, open).apply(block).build())

        fun build(): EntityCollection = EntityCollection(entities)
    }
}
