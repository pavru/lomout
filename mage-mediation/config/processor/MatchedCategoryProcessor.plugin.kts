package processor

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.plugable.MatchedEntityProcessorPlugin

class MatchedCategoryProcessor : MatchedEntityProcessorPlugin() {
    override fun execute(): Map<Attribute, Any?> {
        val v = entities.size == 5
        return emptyMap()
    }
}
