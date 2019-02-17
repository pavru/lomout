package pipeline.assembler

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException

class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(target: Entity, entities: PipelineDataCollection): Map<Attribute, Any?> {
        val idAttr = target.attributes.find { it.name == "entity_id" }
            ?: throw PluginException("Can not find attribute<entity_id>")
        val removeAttr = target.attributes.find { it.name == "remove_flag" }
            ?: throw PluginException("Can not find attribute<remove_flag>")
        val data = entities.find { it.entity.getEntityClass().type == "mage-category" }?.mappedData
            ?: throw PluginException("Can not find entity<mage-category>")
        return mapOf(
            idAttr to data[idAttr],
            removeAttr to true
        )
    }

    override fun execute(): Map<Attribute, Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
