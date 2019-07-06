import MageCategory_conf.MageCategory
import kotlin.reflect.KClass

class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(target: KClass<out Document>, entities: EntityCollection): Map<Attribute, Any> {
        try {
            val category = entities[MageCategory::class] as MageCategory
            return mapOf(
                MageCategory.attributes.getValue("entity_id") to category.entity_id,
                MageCategory.attributes.getValue("remove_flag") to true
            )
        } catch (e: Exception) {
            throw AppConfigException(badPlace(target), e.message, e)
        }
    }
}
