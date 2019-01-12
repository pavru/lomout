package net.pototskiy.apps.magemediation.dsl.config.loader.dataset

import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.loader.dataset.DataSourceConfiguration

@ConfigDsl
class DataSourceConfigurationsBuilder {
    private val sources = mutableListOf<DataSourceConfiguration>()

    @Suppress("unused")
    fun DataSourceConfigurationsBuilder.source(block: DataSourceConfigurationBuilder.() -> Unit) {
        sources.add(DataSourceConfigurationBuilder().apply(block).build())
    }

    fun build(): List<DataSourceConfiguration> = sources.toList()
}