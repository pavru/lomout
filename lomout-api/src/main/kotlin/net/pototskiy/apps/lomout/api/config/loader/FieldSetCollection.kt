package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.EntityType

data class FieldSetCollection(private val sets: List<FieldSet>) : List<FieldSet> by sets {
    val mainSet: FieldSet
        get() = this.find { it.mainSet }!!

    @ConfigDsl
    class Builder(
        private val helper: ConfigBuildHelper,
        private val entityType: EntityType,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?
    ) {
        private val fieldSets = mutableListOf<FieldSet>()

        @ConfigDsl
        fun main(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    helper,
                    entityType,
                    name,
                    true,
                    withSourceHeaders,
                    sources,
                    headerRow
                ).apply(block).build()
            )

        @ConfigDsl
        fun extra(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    helper,
                    entityType,
                    name,
                    false,
                    withSourceHeaders,
                    sources,
                    headerRow
                ).apply(block).build()
            )

        fun build(): FieldSetCollection {
            if (!fieldSets.any { it.mainSet }) {
                throw AppConfigException("Field set collection must contain main set")
            }
            return FieldSetCollection(fieldSets)
        }
    }
}
