package transformer

import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin

public class OnecGroupToLong : NewValueTransformPlugin<String, Long?>() {
    override var value: String = ""

    override fun execute(): Long? {
        return value.drop(1).toLongOrNull()
    }
}
