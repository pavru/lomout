package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass

abstract class ProductEntityClass(
    table: ProductTable,
    entityClass: Class<ProductEntity>? = null
) : SourceDataEntityClass<ProductEntity>(table, entityClass)