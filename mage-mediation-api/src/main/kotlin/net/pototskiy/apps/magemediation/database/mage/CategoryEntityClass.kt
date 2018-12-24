package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass

abstract class CategoryEntityClass(
    table: CategoryTable,
    entityClass: Class<CategoryEntity>? = null
) : SourceDataEntityClass<CategoryEntity>(table, entityClass)