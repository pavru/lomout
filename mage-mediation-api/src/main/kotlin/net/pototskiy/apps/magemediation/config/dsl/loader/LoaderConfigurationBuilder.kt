package net.pototskiy.apps.magemediation.config.dsl.loader

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.dsl.loader.dataset.DatasetConfigurationsBuilder
import net.pototskiy.apps.magemediation.config.newOne.loader.DataFileConfiguration
import net.pototskiy.apps.magemediation.config.newOne.loader.LoaderConfiguration
import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.DatasetConfiguration

@ConfigDsl
class LoaderConfigurationBuilder {
    private var files: List<DataFileConfiguration>? = null
    private var datasets: List<DatasetConfiguration>? = null

    /**
     * Data source files, can be defined like this:
     * * *id* **isIdFor** *path*
     * * *id("id")"* **assignedForPath** *"path"*
     * * *id("path")***.assignedForPath("path")**
     * * *path("path")* **linkedToID** *id("id")*
     * * *path("path")***.linkedToID("id")**
     */
    @Suppress("unused")
    fun LoaderConfigurationBuilder.files(block: DataFilesConfigurationsBuilder.() -> Unit) {
        files = DataFilesConfigurationsBuilder().apply(block).build()
    }

    fun LoaderConfigurationBuilder.datasets(block: DatasetConfigurationsBuilder.() -> Unit) {
        datasets = DatasetConfigurationsBuilder().apply(block).build()
    }

    fun build(): LoaderConfiguration {
        val files = this.files ?: throw ConfigException("Files is not defined in loader section")
        val datasets = this.datasets ?: throw ConfigException("Datasets is not defined in loader section")
        validateSourceFile()
        return LoaderConfiguration(files, datasets)
    }

    private fun validateSourceFile() {
        datasets?.let { datasets ->
            files?.let { files ->
                val usedIDs = datasets.flatMap { it.sources }.map { it.fileId }
                if(!files.map { it.id }.containsAll(usedIDs)) {
                    val wrongIDs = usedIDs.minus(files.map { it.id })
                    throw ConfigException("File ids<${wrongIDs.joinToString(", ")}> are not configured")
                }
            }
        }
    }
}
