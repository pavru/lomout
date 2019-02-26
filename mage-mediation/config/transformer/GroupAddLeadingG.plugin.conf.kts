import net.pototskiy.apps.magemediation.api.plugable.ValueTransformPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException

public class GroupAddLeadingG : ValueTransformPlugin<String?, String>() {

    override fun transform(value: String?): String {
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
