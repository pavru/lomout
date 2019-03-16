package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.AppEntityTypeException
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.config.ConfigDsl

data class InputEntityCollection(private val entities: List<InputEntity>) : List<InputEntity> by entities {

    @ConfigDsl
    class Builder(val helper: ConfigBuildHelper) {
        private val entities = mutableListOf<InputEntity>()

        @Suppress("unused")
        fun Builder.entity(name: String, block: InputEntity.Builder.() -> Unit = {}) {
            val entity = helper.typeManager.getEntityType(name)
                ?: throw AppEntityTypeException("Entity<$name> has not been defined yet")
            entities.add(
                InputEntity
                    .Builder(helper, entity)
                    .apply(block)
                    .build()
            )
        }

        fun build(): InputEntityCollection {
            if (entities.isEmpty()) {
                throw AppEntityTypeException("At least one input entity must be defined")
            }
            return InputEntityCollection(entities)
        }
    }
}
