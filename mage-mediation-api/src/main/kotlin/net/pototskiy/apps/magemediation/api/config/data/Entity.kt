package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.ENTITY_TYPE_NAME_LENGTH
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.NamedObject

data class Entity(
    override val name: String,
    val parents: EntityCollection,
    val excludes: Map<Entity, List<Attribute>>,
    val open: Boolean,
    val ownAttributes: AttributeCollection
) : NamedObject {
    val attributes: AttributeCollection
        get() = AttributeCollection(parents.map { entity ->
            entity.attributes.filterNot { excludes[entity]?.contains(it) == true }
        }.flatten().plus(ownAttributes))

    class Builder(private val name: String, private var open: Boolean = false) {
        private var attributes = mutableListOf<Attribute>()
        private var parents = mutableListOf<Entity>()
        private var excludes = mutableMapOf<Entity, List<Attribute>>()

        @Suppress("unused")
        fun Builder.setOpen() {
            open = true
        }

        @Suppress("unused")
        fun Builder.setClose() {
            open = false
        }

        @Suppress("unused")
        fun Builder.attribute(name: String, block: Attribute.Builder.() -> Unit) =
            attributes.add(Attribute.Builder(name).apply(block).build())

        @Suppress("unused")
        fun Builder.inheritFrom(name: String, block: InheritExcludeBuilder.() -> Unit = {}) {
            val entity = Config.Builder.definedEntities.findRegistered(name, true)
                ?: throw ConfigException("Entity<$name> must be defined before inherit from it")
            parents.add(entity)
            excludes.putAll(InheritExcludeBuilder(entity).apply(block).build())
        }

        fun build(): Entity {
            if (name.length > ENTITY_TYPE_NAME_LENGTH) {
                throw ConfigException("Entity name length must be less or equal $ENTITY_TYPE_NAME_LENGTH")
            }
            val entity  = Entity(
                name,
                EntityCollection(parents),
                excludes,
                open,
                AttributeCollection(attributes)
            )
            Config.Builder.definedEntities.register(entity)
            return entity
        }

        class InheritExcludeBuilder(private val entity: Entity) {
            private val excludes = mutableMapOf<Entity, List<Attribute>>()

            @Suppress("unused")
            fun InheritExcludeBuilder.exclude(vararg name: String) {
                val notFound = name.toList().minus(entity.attributes.map { it.name })
                if (notFound.isNotEmpty()) {
                    throw ConfigException("Entity<${entity.name}> has no attributes<${notFound.joinToString(",")}>")
                }
                excludes[entity] = (excludes[entity] ?: emptyList())
                    .plus(entity.attributes.filter { it.name in name })
            }

            fun build(): Map<Entity, List<Attribute>> = excludes
        }
    }
}
