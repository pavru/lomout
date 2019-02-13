package transformer

import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException

public class GroupAddLeadingG : NewValueTransformPlugin<String?, String>() {
    override var value: String? = null

    override fun execute(): String {
        return value?.let {
            if (it.isNotBlank()) {
                val intID = it.toLongOrNull()
                    ?: throw PluginException("Group code<$value> can not be transformed, it is not integer number")
                String.format("G%03d", intID)
            } else {
                null
            }
        } ?: "G"
    }
}
