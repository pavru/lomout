package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.Type

abstract class UnMatchedEntityProcessorPlugin : Plugin() {
    abstract fun process(entity: DbEntity): Map<AnyTypeAttribute, Type?>
}

typealias UnMatchedEntityProcessorFunction = (DbEntity) -> Map<AnyTypeAttribute, Type?>
