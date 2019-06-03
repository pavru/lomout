package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.unknownPlace

/**
 * Pipeline data collection. Cross lines has server entities, union lines has only one entity
 *
 * @property data List<PipelineData>
 * @constructor
 */
class PipelineDataCollection(private val data: List<PipelineData>) : List<PipelineData> by data {
    /**
     * Get entity by type name
     *
     * @param type String The entity type name
     * @return PipelineData
     * @throws AppConfigException The is no entity for given type
     */
    operator fun get(type: String): PipelineData = this.find { it.entity.eType.name == type }
        ?: throw AppConfigException(unknownPlace(), "Pipeline data does not contain entity '$type'.")

    /**
     * Get entity by type name
     *
     * @param type String The entity type name
     * @return PipelineData?
     */
    @PublicApi
    fun getEntityOrNull(type: String): PipelineData? = this.find { it.entity.eType.name == type }
}
