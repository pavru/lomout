import kotlin.collections.set

class MatchedCategoryAssembler : PipelineAssemblerPlugin() {
    override fun assemble(
        target: EntityType,
        entities: PipelineDataCollection
    ): Map<AnyTypeAttribute, Type?> {
        val data = mutableMapOf<AnyTypeAttribute, Type?>()
        try {
            val mageCategory = entities["mage-category"]
            val onecGroup = entities["onec-group"]
            target.attributes.forEach { targetAttr ->
                if (mageCategory[targetAttr.name] != null) {
                    data[targetAttr] = mageCategory[targetAttr.name]
                } else if (onecGroup[targetAttr.name] != null) {
                    data[targetAttr] = onecGroup[targetAttr.name]
                }
            }
            data[target.getAttribute("remove_flag")] = BooleanType(false)
        } catch (e: Exception) {
            throw AppPluginException(e.message, e)
        }
        return data
    }
}
