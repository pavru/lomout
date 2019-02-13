package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

data class MediatorConfiguration(
    val lines: ProductionLineCollection
) {
    @ConfigDsl
    class Builder {
        private var lines = mutableListOf<ProductionLine>()

        @Suppress("unused")
        fun Builder.productionLine(block: ProductionLine.Builder.() -> Unit) {
            lines.add(ProductionLine.Builder().apply(block).build())
        }

        fun build(): MediatorConfiguration {
            return MediatorConfiguration(ProductionLineCollection(lines))
        }
    }
}
