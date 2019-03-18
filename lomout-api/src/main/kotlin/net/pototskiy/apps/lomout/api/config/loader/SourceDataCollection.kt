package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl

data class SourceDataCollection(private val sourceData: List<SourceData>) : List<SourceData> by sourceData {
    @ConfigDsl
    class Builder(val helper: ConfigBuildHelper) {
        private val sourceData = mutableListOf<SourceData>()

        @Suppress("unused")
        fun Builder.source(block: SourceData.Builder.() -> Unit) =
            sourceData.add(SourceData.Builder(helper).apply(block).build())

        fun build(): SourceDataCollection = SourceDataCollection(sourceData)
    }
}
