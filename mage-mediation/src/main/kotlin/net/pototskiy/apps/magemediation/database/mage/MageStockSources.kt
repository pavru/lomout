package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.api.database.mage.StockSourceEntity
import net.pototskiy.apps.magemediation.api.database.mage.StockSourceEntityClass
import net.pototskiy.apps.magemediation.api.database.mage.StockSourceTable
import org.jetbrains.exposed.dao.EntityID

object MageStockSources : StockSourceTable("mage_stock_source")

class MageStockSource(id: EntityID<Int>) : StockSourceEntity(id) {
    companion object : StockSourceEntityClass(MageStockSources)

    override var sourceCode by MageStockSources.sourceCode
    override var sku by MageStockSources.sku
    override var status by MageStockSources.status
    override var quantity by MageStockSources.quantity
}
