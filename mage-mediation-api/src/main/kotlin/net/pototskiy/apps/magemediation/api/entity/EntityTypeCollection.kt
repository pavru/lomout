package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

class EntityTypeCollection(private val value: List<EntityType>) : List<EntityType> by value {
    @ConfigDsl
    class Builder(private val typeManager: EntityTypeManager) {
        private val eTypes = mutableListOf<EntityType>()

        fun entity(name: String, open: Boolean, block: EntityType.Builder.() -> Unit) =
                eTypes.add(EntityType.Builder(typeManager, name, open).apply(block).build())

        fun build(): EntityTypeCollection = EntityTypeCollection(eTypes)
    }
}
