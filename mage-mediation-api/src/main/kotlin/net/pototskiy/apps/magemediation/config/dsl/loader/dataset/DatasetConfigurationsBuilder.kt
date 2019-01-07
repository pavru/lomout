package net.pototskiy.apps.magemediation.config.dsl.loader.dataset

import net.pototskiy.apps.magemediation.config.DatasetTarget
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.DatasetConfiguration

@ConfigDsl
class DatasetConfigurationsBuilder {
    private val datasets = mutableListOf<DatasetConfiguration>()

    fun DatasetConfigurationsBuilder.dataset(
        name: String? = null,
        headersRow: Int? = null,
        rowsToSkip: Int = 0,
        maxAbsentDays: Int = 5,
        target: DatasetTarget? = null,
        block: DatasetConfigurationBuilder.() -> Unit
    ) = datasets.add(
        DatasetConfigurationBuilder(
            name,
            headersRow,
            rowsToSkip,
            maxAbsentDays,
            target
        ).apply(block).build()
    )

    fun build(): List<DatasetConfiguration> = datasets.toList()
}