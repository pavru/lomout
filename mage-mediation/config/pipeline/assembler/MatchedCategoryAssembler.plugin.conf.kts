import net.pototskiy.apps.magemediation.api.plugable.*
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.config.mediator.*

class MatchedCategoryAssembler : PipelineAssemblerPlugin() {
    override fun assemble(
        target: EType,
        entities: PipelineDataCollection
    ): Map<AnyTypeAttribute, Type?> {
        val data = mutableMapOf<AnyTypeAttribute, Type?>()
        entities.find { it.entity.eType.type == "mage-category" }
            ?.extData?.forEach { (key, value) -> data[key] = value }
        entities.find { it.entity.eType.type == "onec-group" }
            ?.extData?.forEach { (key, value) ->
            if (data.containsKey(key)) data[key] = value
        }
        return data
    }
}
