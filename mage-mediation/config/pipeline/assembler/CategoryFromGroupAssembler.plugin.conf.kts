public class CategoryFromGroupAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: EType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?> {
        val data = mutableMapOf<AnyTypeAttribute, Type?>()
        entities.find { it.entity.eType.type == "onec-group" }
            ?.extData?.forEach { (key, value) ->
            if (target.attributes.contains(key)) data[key] = value
        }
        return data
    }
}
