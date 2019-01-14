package net.pototskiy.apps.magemediation.api.database.mdtn.category

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataEntityClass

abstract class MCategoryEntityClass(
    table: MCategoryTable,
    entityClass: Class<MCategoryEntity>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : MediumDataEntityClass<MCategoryEntity>(table, entityClass, *attrEntityClass)