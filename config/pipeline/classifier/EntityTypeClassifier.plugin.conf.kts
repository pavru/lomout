import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement

class EntityTypeClassifier : PipelineClassifierPlugin() {
    var typeList: List<String> = emptyList()

    override fun classify(element: ClassifierElement): ClassifierElement {
        return if (element.entities.any { it.type.name in typeList }) {
            element.match()
        } else {
            element.mismatch()
        }
    }
}
