package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.medium.MediatorException

class PipelineDataCollection(private val data: List<PipelineData>) : List<PipelineData> by data {
    operator fun get(type: String): PipelineData = this.find { it.entity.eType.name == type }
        ?: throw MediatorException("Pipeline data does not contains entity<$type>")

    @PublicApi
    fun getEntityOrNull(type: String): PipelineData? = this.find { it.entity.eType.name == type }
}
