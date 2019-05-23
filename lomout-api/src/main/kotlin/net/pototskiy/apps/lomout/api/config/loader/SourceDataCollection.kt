package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl

/**
 * Source data collection
 *
 * @property sourceData List<SourceData>
 * @constructor
 */
data class SourceDataCollection(private val sourceData: List<SourceData>) : List<SourceData> by sourceData {
    /**
     * Source data collection builder class
     *
     * @property helper The config build helper
     * @property sourceData MutableList<SourceData>
     * @constructor
     */
    @ConfigDsl
    class Builder(val helper: ConfigBuildHelper) {
        private val sourceData = mutableListOf<SourceData>()

        /**
         * Define sources collection
         *
         * ```
         * ...
         *  sources {
         *      source { file(..); sheet(...); }
         *      source { file(..); sheet(...); }
         *      ...
         *  }
         * ...
         * ```
         *
         * @receiver Builder
         * @param block The source definition
         * @return Boolean
         */
        @Suppress("unused")
        fun Builder.source(block: SourceData.Builder.() -> Unit) =
            sourceData.add(SourceData.Builder(helper).apply(block).build())

        /**
         * Build sources collection
         *
         * @return SourceDataCollection
         */
        fun build(): SourceDataCollection = SourceDataCollection(sourceData)
    }
}
