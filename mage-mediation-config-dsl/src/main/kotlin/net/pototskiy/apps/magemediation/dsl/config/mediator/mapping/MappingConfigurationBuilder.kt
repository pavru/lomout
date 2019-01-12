package net.pototskiy.apps.magemediation.dsl.config.mediator.mapping

import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.mediator.mapping.CategoryMappingConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.mapping.MappingConfiguration

@ConfigDsl
class MappingConfigurationBuilder {
    private var catMapping: CategoryMappingConfiguration? = null

    fun MappingConfigurationBuilder.categories(block: CategoryMappingConfigurationBuilder.()->Unit) {
        catMapping = CategoryMappingConfigurationBuilder().apply(block).build()
    }

    fun build(): MappingConfiguration {
        return MappingConfiguration(
            catMapping ?: emptyCatMapping
        )
    }

    companion object {
        val emptyCatMapping = CategoryMappingConfiguration(
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap(),
            emptyMap()
        )
    }
}
