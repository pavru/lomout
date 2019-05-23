package net.pototskiy.apps.lomout.api.config.pipeline

import net.pototskiy.apps.lomout.api.config.mediator.PipelineData
import org.jetbrains.exposed.dao.EntityID

interface PipelineDataCache {
    fun readEntity(id: EntityID<Int>): PipelineData
}
