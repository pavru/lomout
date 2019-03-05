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
                if (mageCategory[targetAttr.name.attributeName] != null) {
                    data[targetAttr] = mageCategory[targetAttr.name.attributeName]
                } else if (onecGroup[targetAttr.name.attributeName] != null) {
                    data[targetAttr] = onecGroup[targetAttr.name.attributeName]
                }
            }
            data[target.getAttribute("remove_flag")] = BooleanValue(false)
        } catch (e: Exception) {
            throw PluginException(e.message, e)
        }
        return data
    }
}
