package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass

abstract class CustomerGroupEntityClass(
    table: CustomerGroupTable,
    entityClass: Class<CustomerGroupEntity>? = null
) : SourceDataEntityClass<CustomerGroupEntity>(table, entityClass)