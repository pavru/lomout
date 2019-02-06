package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.EntityCollection
import net.pototskiy.apps.magemediation.api.config.mediator.mage.MageMediatorConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.mapping.MappingConfiguration
import net.pototskiy.apps.magemediation.api.config.mediator.onec.OnecMediatorConfiguration

data class MediatorConfiguration(
    val entities: EntityCollection,
    val onec: OnecMediatorConfiguration,
    val magento: MageMediatorConfiguration,
    val mapping: MappingConfiguration
) {
    class Builder {
        private var onecConf: OnecMediatorConfiguration? = null
        private var mageConf: MageMediatorConfiguration? = null
        private var mapping: MappingConfiguration? = null
        private var entities: EntityCollection? = null

        @Suppress("unused")
        fun Builder.entities(block: EntityCollection.Builder.()->Unit) {
            entities = EntityCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.onec(block: OnecMediatorConfiguration.Builder.() -> Unit) {
            onecConf = OnecMediatorConfiguration.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.magento(block: MageMediatorConfiguration.Builder.() -> Unit) {
            mageConf = MageMediatorConfiguration.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.mapping(block: MappingConfiguration.Builder.() -> Unit) {
            mapping = MappingConfiguration.Builder().apply(block).build()
        }

        fun build(): MediatorConfiguration {
            return MediatorConfiguration(
                entities
                    ?: throw ConfigException("Mediator configuration has no entity definitions"),
                onecConf
                    ?: throw ConfigException("Mediator section must include OneC configuration"),
                mageConf
                    ?: throw ConfigException("Mediator section must include Magento configuration"),
                mapping ?: MappingConfiguration.Builder().build()
            )
        }
    }
}
