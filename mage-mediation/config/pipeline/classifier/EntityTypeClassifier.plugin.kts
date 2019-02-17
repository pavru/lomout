package pipeline.classifier

import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierPlugin

class EntityTypeClassifier : PipelineClassifierPlugin() {
    private var typeList = listOf<String>()
    override fun execute(): Pipeline.CLASS {
        if (entities.any { it.entity.getEntityClass().type in typeList }) {
            return Pipeline.CLASS.MATCHED
        } else {
            return Pipeline.CLASS.UNMATCHED
        }
    }

    override fun setOptions(options: NewPlugin.Options) {
        super.setOptions(options)
        (options as? Options)?.let {
            this.typeList = it.typeList ?: listOf<String>()
        }
    }

    override fun optionSetter(): NewPlugin.Options {
        return Options()
    }

    class Options : PipelineClassifierPlugin.Options() {
        var typeList: List<String>? = null
    }
}
