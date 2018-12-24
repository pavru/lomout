package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID

abstract class StockSourceEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var sourceCode: String
    abstract var sku: String
    abstract var status: Boolean
    abstract var quantity: Double
}