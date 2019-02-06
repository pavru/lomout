package net.pototskiy.apps.magemediation.api.config.mediator.mapping

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

class MappingConfiguration(
    val category: CategoryMappingConfiguration
) {
    @ConfigDsl
    class Builder {
        private var catMapping: CategoryMappingConfiguration? = null

        @Suppress("unused")
        fun Builder.categories(block: CategoryMappingConfiguration.Builder.() -> Unit) {
            catMapping = CategoryMappingConfiguration.Builder().apply(block).build()
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
                emptyMap(),
                emptyMap(),
                emptyMap()
            )
        }
    }
}
