package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

data class SourceDataCollection(private val sourceData: List<SourceData>) : List<SourceData> by sourceData {
    @ConfigDsl
    class Builder() {
        private val sourceData = mutableListOf<SourceData>()

        @Suppress("unused")
        fun Builder.source(block: SourceData.Builder.() -> Unit) =
            sourceData.add(SourceData.Builder().apply(block).build())

        fun build(): SourceDataCollection = SourceDataCollection(sourceData)
    }
}
