import OnecGroup_conf.OnecGroup
import kotlin.reflect.KClass

class CategoryFromGroupAssembler : PipelineAssemblerPlugin() {
    override fun assemble(target: KClass<out Document>, entities: EntityCollection): Map<Attribute, Any> {
        val data = mutableMapOf<Attribute, Any>()
        entities.getOrNull(OnecGroup::class)?.let { onec ->
            target.documentMetadata.attributes.values.forEach { attr ->
                onec.getAttribute(attr)?.let { data[attr] = it }
            }
        }
        return data
    }
}
