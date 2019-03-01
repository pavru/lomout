class EntityTypeClassifier : PipelineClassifierPlugin() {
    var typeList: List<String> = emptyList()

    override fun classify(entities: PipelineDataCollection): Pipeline.CLASS {
        return if (entities.any { it.entity.eType.type in typeList }) {
            Pipeline.CLASS.MATCHED
        } else {
            Pipeline.CLASS.UNMATCHED
        }
    }
}
