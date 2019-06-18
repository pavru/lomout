import kotlin.collections.set

class MatchedCategoryAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: EntityType, entities: EntityCollection): Map<AnyTypeAttribute, Type> {
        val data = mutableMapOf<AnyTypeAttribute, Type>()
        try {
            val mageCategory = entities["mage-category"]
            val onecGroup = entities["onec-group"]
            target.attributes.forEach { targetAttr ->
                if (mageCategory[targetAttr.name] != null) {
                    data[targetAttr] = mageCategory[targetAttr.name]!!
                } else if (onecGroup[targetAttr.name] != null) {
                    data[targetAttr] = onecGroup[targetAttr.name]!!
                }
            }
            data[target.getAttribute("remove_flag")] = BOOLEAN(false)
        } catch (e: Exception) {
            throw AppDataException(badPlace(target), e.message, e)
        }
        return data
    }
}
