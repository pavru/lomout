class CategoryFromGroupAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: EntityType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?> {
        val data = mutableMapOf<AnyTypeAttribute, Type?>()
        entities.find { it.entity.eType.name == "onec-group" }
            ?.extData?.forEach { (key, value) ->
            if (target.attributes.contains(key)) data[key] = value
        }
        return data
    }
}
