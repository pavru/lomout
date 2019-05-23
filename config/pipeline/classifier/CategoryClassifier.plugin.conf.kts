import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement

class CategoryClassifier : PipelineClassifierPlugin() {
    override fun classify(element: ClassifierElement): ClassifierElement {
        try {
            val entities = element.entities
            val group = entities["onec-group"]
            val category = entities["mage-category"]
            val categoryPath = category["__path"]
            if (categoryPath?.value in rootMageCategories) return element.mismatch()
            if (group["transformed_path"] == categoryPath) return element.match()
            return element.mismatch()
        } catch (e: Exception) {
            throw AppPluginException(e.message, e)
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
