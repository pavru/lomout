package net.pototskiy.apps.magemediation.plugins.medium

import net.pototskiy.apps.magemediation.api.config.ConfigExt.mapCategoryIDToGroupID
import net.pototskiy.apps.magemediation.api.plugable.Plugin
import net.pototskiy.apps.magemediation.api.plugable.ValueTransformPlugin

class MageIDToGroupCode: ValueTransformPlugin<Long, String> {
    override fun transform(value: Long): String {
        val config = Plugin.config
        val id = config.mapCategoryIDToGroupID(value) ?: value
        return String.format("G%03d",id)
    }
}
