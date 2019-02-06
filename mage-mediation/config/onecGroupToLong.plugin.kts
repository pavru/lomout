
import net.pototskiy.apps.magemediation.api.plugable.valueTransformPlugin

val onecGroupToLongPlugin = valueTransformPlugin<String, Long?> {
    execute {
        value?.drop(1)?.toLongOrNull()
    }
}
