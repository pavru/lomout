import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline
import net.pototskiy.apps.magemediation.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.magemediation.api.plugable.Plugin
import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierPlugin

class EntityTypeClassifier : PipelineClassifierPlugin() {
    var typeList: List<String> = emptyList()

    override fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        if (entities.any { it.entity.eType.type in typeList }) {
            return Pipeline.CLASS.MATCHED
        } else {
            return Pipeline.CLASS.UNMATCHED
        }
    }
}
