package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.AppConfigException
import net.pototskiy.apps.magemediation.api.AppEntityTypeException
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.entity.EntityTypeCollection

data class LoaderConfiguration(
    val files: SourceFileCollection,
    val entities: EntityTypeCollection,
    val loads: LoadCollection
) {
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var files: SourceFileCollection? = null
        private var entities: EntityTypeCollection? = null
        private var loads = mutableListOf<Load>()

        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        fun entities(block: EntityTypeCollection.Builder.() -> Unit) {
            this.entities = EntityTypeCollection.Builder(helper).apply(block).build()
        }

        fun loadEntity(entityType: String, block: Load.Builder.() -> Unit) {
            val entity = helper.typeManager.getEntityType(entityType)
                ?: throw AppEntityTypeException("Define entity<$entityType> before load configuration")
            loads.add(Load.Builder(helper, entity).apply(block).build())
        }

        fun build(): LoaderConfiguration {
            val files = this.files ?: throw AppConfigException("Files is not defined in loader section")
            return LoaderConfiguration(
                files,
                entities ?: throw AppEntityTypeException("At least one entity must be defined"),
                LoadCollection(loads)
            )
        }
    }
}
