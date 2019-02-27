package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.ETypeCollection
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class LoaderConfiguration(
    val files: SourceFileCollection,
    val entities: ETypeCollection,
    val loads: LoadCollection
) {
    @ConfigDsl
    class Builder {
        private var files: SourceFileCollection? = null
        private var entities: ETypeCollection? = null
        private var loads = mutableListOf<Load>()

        @Suppress("unused")
        fun Builder.files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.entities(block: ETypeCollection.Builder.() -> Unit) {
            this.entities = ETypeCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.loadEntity(entityType: String, block: Load.Builder.() -> Unit) {
            val entity = EntityTypeManager.getEntityType(entityType)
                ?: throw ConfigException("Define entity<$entityType> before load configuration")
            loads.add(Load.Builder(entity).apply(block).build())
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
