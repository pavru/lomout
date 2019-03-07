package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class FieldSetCollection(private val sets: List<FieldSet>) : List<FieldSet> by sets {
    class Builder(
        private val typeManager: EntityTypeManager,
        private val entityType: EntityType,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?
    ) {
        private val fieldSets = mutableListOf<FieldSet>()

        fun main(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    typeManager,
                    entityType,
                    name,
                    true,
                    withSourceHeaders,
                    sources,
                    headerRow
                ).apply(block).build()
            )

        fun extra(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    typeManager,
                    entityType,
                    name,
                    false,
                    withSourceHeaders,
                    sources,
                    headerRow
                ).apply(block).build()
            )

        fun build(): FieldSetCollection = FieldSetCollection(fieldSets)
    }
}
