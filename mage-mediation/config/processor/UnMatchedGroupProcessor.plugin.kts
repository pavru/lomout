package processor

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.plugable.UnMatchedEntityProcessorPlugin

class UnMatchedGroupProcessor : UnMatchedEntityProcessorPlugin() {
    override fun execute(): Map<Attribute, Any?> {
        val v = entity.data.size
        return emptyMap()
    }
}
