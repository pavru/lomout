import MageCategory_conf.MageCategory
import OnecGroup_conf.OnecGroup
import kotlin.collections.set
import kotlin.reflect.KClass

class MatchedCategoryAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: KClass<out Document>, entities: EntityCollection): Map<Attribute, Any> {
        val data = mutableMapOf<Attribute, Any>()
        try {
            val mageCategory = entities[MageCategory::class] as MageCategory
            val onecGroup = entities[OnecGroup::class]
            target.documentMetadata.attributes.values.forEach { targetAttr ->
                if (mageCategory.getAttribute(targetAttr.name) != null) {
                    data[targetAttr] = mageCategory.getAttribute(targetAttr.name)!!
                } else if (onecGroup.getAttribute(targetAttr) != null) {
                    data[targetAttr] = onecGroup.getAttribute(targetAttr.name)!!
                }
            }
            data[target.documentMetadata.attributes.getValue("remove_flag")] = false
        } catch (e: Exception) {
            throw AppDataException(badPlace(target), e.message, e)
        }
        return data
    }
}
