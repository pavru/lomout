package pipeline.classifier

import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierPlugin

public class CategoryClassifier : PipelineClassifierPlugin() {
    override fun execute(): Pipeline.CLASS {
        val group = entities.find { it.entity.getEntityClass().type == "onec-group" }
            ?: return Pipeline.CLASS.UNMATCHED
        val category = entities.find { it.entity.getEntityClass().type == "mage-category" }
            ?: return Pipeline.CLASS.UNMATCHED
//        if (group.mappedData[entityIDAttr] == category.origData[entityIDAttr]) return Pipeline.CLASS.MATCHED
        if (group.mappedData[pathAttr] == category.origData[pathAttr]) return Pipeline.CLASS.MATCHED
        return Pipeline.CLASS.UNMATCHED
    }

    companion object {
        private val groupEntityClass by lazy { EntityClass.getClass("onec-group")!! }
        private val categoryEntityClass by lazy { EntityClass.getClass("mage-category")!! }
        private val entityIDAttr by lazy { categoryEntityClass.attributes.find { it.name == "entity_id" }!! }
        private val pathAttr by lazy { categoryEntityClass.attributes.find { it.name == "__path" }!! }
    }
}
