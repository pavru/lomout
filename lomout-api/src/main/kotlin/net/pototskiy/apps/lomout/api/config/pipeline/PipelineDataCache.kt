package net.pototskiy.apps.lomout.api.config.pipeline

import net.pototskiy.apps.lomout.api.config.mediator.PipelineData
import org.jetbrains.exposed.dao.EntityID

/**
 * Pipeline data cache interface
 */
interface PipelineDataCache {
    /**
     * Get entity from pipeline cache
     *
     * @param id The entity ID
     * @return The entity wrapped to [PipelineData]
     */
    fun readEntity(id: EntityID<Int>): PipelineData
}
