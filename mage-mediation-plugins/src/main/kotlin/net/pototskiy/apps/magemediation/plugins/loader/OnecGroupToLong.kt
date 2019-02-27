package net.pototskiy.apps.magemediation.plugins.loader

import net.pototskiy.apps.magemediation.api.plugable.ValueTransformPlugin

class OnecGroupToLong : ValueTransformPlugin<String, Long?> {
    override fun transform(value: String): Long? = value.drop(1).toLongOrNull()
}
