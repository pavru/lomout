package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass

abstract class AdvPriceEntityClass(
    table: AdvPriceTable,
    entityClass: Class<AdvPriceEntity>? = null
) : SourceDataEntityClass<AdvPriceEntity>(table, entityClass)