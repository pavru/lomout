package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass

abstract class StockSourceEntityClass(
    table: StockSourceTable,
    entityClass: Class<StockSourceEntity>? = null
) : SourceDataEntityClass<StockSourceEntity>(table, entityClass)