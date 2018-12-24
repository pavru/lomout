package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID

abstract class ProductEntity(id: EntityID<Int>): SourceDataEntity(id) {
    abstract var sku: String
}