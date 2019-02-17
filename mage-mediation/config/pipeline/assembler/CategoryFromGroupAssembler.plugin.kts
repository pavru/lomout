package pipeline.assembler

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.plugable.PipelineAssemblerPlugin

public class CategoryFromGroupAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: Entity, entities: PipelineDataCollection): Map<Attribute, Any?> {
        val data = mutableMapOf<Attribute, Any?>()
        entities.find { it.entity.getEntityClass().type == "onec-group" }
            ?.mappedData?.forEach { (key, value) ->
            if (target.attributes.contains(key)) data[key] = value
        }
        return data
    }

    // TODO: 17.02.2019 remove later
    override fun execute(): Map<Attribute, Any?> {
        return emptyMap()
    }
}
