package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.EntityTypeCollection
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class LoaderConfiguration(
    val files: SourceFileCollection,
    val entities: EntityTypeCollection,
    val loads: LoadCollection
) {
    @ConfigDsl
    class Builder(private val typeManager: EntityTypeManager) {
        private var files: SourceFileCollection? = null
        private var entities: EntityTypeCollection? = null
        private var loads = mutableListOf<Load>()

        @Suppress("unused")
        fun Builder.files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.entities(block: EntityTypeCollection.Builder.() -> Unit) {
            this.entities = EntityTypeCollection.Builder(typeManager).apply(block).build()
        }

        @Suppress("unused")
        fun Builder.loadEntity(entityType: String, block: Load.Builder.() -> Unit) {
            val entity = typeManager.getEntityType(entityType)
                ?: throw ConfigException("Define entity<$entityType> before load configuration")
            loads.add(Load.Builder(typeManager, entity).apply(block).build())
        }

        fun build(): LoaderConfiguration {
            val files = this.files ?: throw ConfigException("Files is not defined in loader section")
            return LoaderConfiguration(
                files,
                entities ?: throw ConfigException("At least one entity must be defined"),
                LoadCollection(loads)
            )
        }
    }
}
