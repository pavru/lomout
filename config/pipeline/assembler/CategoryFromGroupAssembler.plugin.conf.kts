class CategoryFromGroupAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: EntityType, entities: EntityCollection): Map<AnyTypeAttribute, Type> {
        val data = mutableMapOf<AnyTypeAttribute, Type>()
        entities.getOrNull("onec-group")?.let { onec ->
            target.attributes.forEach { attr ->
                onec[attr]?.let { data[attr] = it }
            }
        }
        return data
    }
}
