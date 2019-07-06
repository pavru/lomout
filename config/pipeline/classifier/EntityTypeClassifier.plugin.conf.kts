import kotlin.reflect.KClass

class EntityTypeClassifier : PipelineClassifierPlugin() {
    var typeList: List<KClass<out Document>> = emptyList()

    override fun classify(element: ClassifierElement): ClassifierElement {
        return if (element.entities.any { it::class in typeList }) {
            element.match()
        } else {
            element.mismatch()
        }
    }
}
