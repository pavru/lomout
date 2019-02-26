package net.pototskiy.apps.magemediation.api.config.mediator

class PipelineDataCollection(private val data: List<PipelineData>) : List<PipelineData> by data {
    operator fun get(type: String): PipelineData? = this.find { it.entity.eType.type == type }
}

