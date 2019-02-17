package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity

class PipelineData(
    val entity: PersistentSourceEntity,
    private val inputEntity: InputEntity
) {
    val origData = entity.data
    val mappedData by lazy { inputEntity.mapAttributes(entity) }
}
