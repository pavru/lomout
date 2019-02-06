package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.EntityCollection

data class LoaderConfiguration(
    val files: SourceFileCollection,
    val datasets: List<LoaderDataset>,
    val entities: EntityCollection,
    val loads: LoadCollection
) {
    @ConfigDsl
    class Builder {
        private var files: SourceFileCollection? = null
        private var datasets = mutableListOf<LoaderDataset>()
        private var entities: EntityCollection? = null
        private var loads = mutableListOf<Load>()

        @Suppress("unused")
        fun Builder.files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.entities(block: EntityCollection.Builder.() -> Unit) {
            this.entities = EntityCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.loadEntity(name: String, block: Load.Builder.() -> Unit) {
            val entity = entities?.find { it.name == name }
                ?: throw ConfigException("Define entity<$name> before load configuration")
            loads.add(Load.Builder(entity).apply(block).build())
        }

        fun build(): LoaderConfiguration {
            val files = this.files ?: throw ConfigException("Files is not defined in loader section")
            val datasets = this.datasets
            return LoaderConfiguration(
                files,
                datasets,
                entities ?: throw ConfigException("At least one entity must be defined"),
                LoadCollection(loads)
            )
        }
    }
}
