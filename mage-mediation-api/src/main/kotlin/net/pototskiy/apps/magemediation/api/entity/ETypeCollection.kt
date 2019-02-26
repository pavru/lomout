package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.config.ConfigDsl

class ETypeCollection(private val value: List<EType>): List<EType> by value {
    @ConfigDsl
    class Builder {
        private val etypes = mutableListOf<EType>()

        fun entity(name: String, open: Boolean, block: EType.Builder.()->Unit) =
                etypes.add(EType.Builder(name,open).apply(block).build())

        fun build(): ETypeCollection = ETypeCollection(etypes)
    }
}
