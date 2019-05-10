package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl

/**
 * Mediator configuration
 *
 * @property lines ProductionLineCollection The production lines  collection
 * @constructor
 */
data class MediatorConfiguration(
    val lines: ProductionLineCollection
) {
    /**
     * Mediator configuration builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property lines MutableList<ProductionLine> The production lines collection
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var lines = mutableListOf<ProductionLine>()

        /**
         * Define union production line
         *
         * ```
         * ...
         *  unionProductionLine {
         *      input {...}
         *      output {...}
         *      pipeline {...}
         *  }
         * ...
         * ```
         * * [input][ProductionLine.Builder.input] - define input entities, **mandatory**
         * * [output][ProductionLine.Builder.output] - define new output entity or reference to existing, **mandatory**
         * * [pipeline][ProductionLine.Builder.pipeline] - define root pipeline of production line, **mandatory**
         *
         * @see AbstractLine.LineType
         * @param block ProductionLine.Builder.() -> Unit
         */
        fun unionProductionLine(block: ProductionLine.Builder.() -> Unit) {
            lines.add(ProductionLine.Builder(helper, AbstractLine.LineType.UNION).apply(block).build())
        }

        /**
         * Define cross production line
         *
         * ```
         * ...
         *  crossProductionLine {
         *      input {...}
         *      output {...}
         *      pipeline {...}
         *  }
         * ...
         * ```
         * * [input][ProductionLine.Builder.input] - define input entities, **mandatory**
         * * [output][ProductionLine.Builder.output] - define new output entity or reference to existing, **mandatory**
         * * [pipeline][ProductionLine.Builder.pipeline] - define root pipeline of production line, **mandatory**
         *
         * @see AbstractLine.LineType
         * @param block ProductionLine.Builder.() -> Unit
         */
        fun crossProductionLine(block: ProductionLine.Builder.() -> Unit) {
            lines.add(ProductionLine.Builder(helper, AbstractLine.LineType.CROSS).apply(block).build())
        }

        /**
         * Build mediator configuration
         * @return MediatorConfiguration
         */
        fun build(): MediatorConfiguration {
            return MediatorConfiguration(ProductionLineCollection(lines))
        }
    }
}
