public class CategoryClassifier : PipelineClassifierPlugin() {
    override fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        val group = entities["onec-group"] ?: return Pipeline.CLASS.UNMATCHED
        val category = entities["mage-category"] ?: return Pipeline.CLASS.UNMATCHED
        val categoryPath = category["__path"]
        if ( categoryPath?.value in rootMageCategories) return Pipeline.CLASS.UNMATCHED
        if (group["transformed_path"] == categoryPath) return Pipeline.CLASS.MATCHED
        return Pipeline.CLASS.UNMATCHED
    }

    companion object {
        private val rootMageCategories = listOf(
            "/Root Catalog",
            "/Root Catalog/Default Category",
            "/Root Catalog/Default Category/Каталог"
        )
    }
}
