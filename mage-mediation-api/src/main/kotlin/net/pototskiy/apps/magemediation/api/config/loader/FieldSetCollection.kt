package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.data.AttributeCollection

data class FieldSetCollection(private val sets: List<FieldSet>) : List<FieldSet> by sets {
    class Builder(private val attributes: AttributeCollection, private val withSourceHeaders: Boolean) {
        private val fieldSets = mutableListOf<FieldSet>()

        @Suppress("unused")
        fun Builder.main(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(FieldSet.Builder(name, true, attributes, withSourceHeaders).apply(block).build())

        @Suppress("unused")
        fun Builder.extra(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(FieldSet.Builder(name, false, attributes, withSourceHeaders).apply(block).build())

        fun build(): FieldSetCollection = FieldSetCollection(fieldSets)
    }
}
