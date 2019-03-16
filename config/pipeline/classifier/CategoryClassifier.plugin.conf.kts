class CategoryClassifier : PipelineClassifierPlugin() {
    override fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        try {
            val group = entities["onec-group"]
            val category = entities["mage-category"]
            val categoryPath = category["__path"]
            if (categoryPath?.value in rootMageCategories) return Pipeline.CLASS.UNMATCHED
            if (group["transformed_path"] == categoryPath) return Pipeline.CLASS.MATCHED
            return Pipeline.CLASS.UNMATCHED
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
