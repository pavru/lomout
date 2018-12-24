package net.pototskiy.apps.magemediation.database.mdtn.category

import net.pototskiy.apps.magemediation.database.mdtn.MediumDataEntityClass

abstract class CategoryEntityClass(
    table: CategoryTable,
    entityClass: Class<CategoryEntity>? = null
) : MediumDataEntityClass<CategoryEntity>(table, entityClass)