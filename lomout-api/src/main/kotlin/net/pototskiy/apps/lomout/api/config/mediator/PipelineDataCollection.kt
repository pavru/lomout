package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi

class PipelineDataCollection(private val data: List<PipelineData>) : List<PipelineData> by data {
    operator fun get(type: String): PipelineData = this.find { it.entity.eType.name == type }
        ?: throw AppConfigException("Pipeline data does not contains entity<$type>")

    @PublicApi
    fun getEntityOrNull(type: String): PipelineData? = this.find { it.entity.eType.name == type }
}
