import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.plugable.valueTransformPlugin

val groupAddLeadingGPlugin = valueTransformPlugin<String, String> {
    execute {
        value?.let {
            if ( it.isNotBlank()) {
            val intID = it.toLongOrNull()
                ?: throw PluginException("Group code<$value> can not be transformed, it is not integer number")
            String.format("G%03d", intID)
            } else {
                null
            }
        } ?: ""
    }
}
