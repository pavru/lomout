package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity

abstract class UnMatchedEntityProcessorPlugin : NewPlugin<Map<Attribute, Any?>>() {
    protected lateinit var entity: PersistentSourceEntity

    fun process(entity: PersistentSourceEntity): Map<Attribute, Any?> {
        this.entity = entity
        return execute()
    }

    open class Options : NewPlugin.Options()
}

typealias UnMatchedEntityProcessorFunction = (PersistentSourceEntity) -> Map<Attribute, Any?>
