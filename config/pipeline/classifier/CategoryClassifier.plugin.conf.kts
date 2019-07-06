import MageCategory_conf.MageCategory
import OnecGroup_conf.OnecGroup

class CategoryClassifier : PipelineClassifierPlugin() {
    override fun classify(element: ClassifierElement): ClassifierElement {
        try {
            val entities = element.entities
            val group = entities[OnecGroup::class] as OnecGroup
            val category = entities[MageCategory::class] as MageCategory
            val categoryPath = category.__path
            if (categoryPath in rootMageCategories) return element.mismatch()
            if (group.transformed_path == categoryPath) return element.match()
            return element.mismatch()
        } catch (e: Exception) {
            throw AppDataException(unknownPlace(), e.message, e)
        }
    }

    companion object {
        private val rootMageCategories = listOf(
            "/Root Catalog",
            "/Root Catalog/Default Category",
            "/Root Catalog/Default Category/Каталог"
        )
    }
}
