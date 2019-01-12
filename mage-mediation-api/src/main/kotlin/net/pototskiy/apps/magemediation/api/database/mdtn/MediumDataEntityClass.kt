package net.pototskiy.apps.magemediation.api.database.mdtn

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.source.DataEntityWithAttributeClass

abstract class MediumDataEntityClass<out E : MediumDataEntity>(
    table: MediumDataTable,
    entityClass: Class<E>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : DataEntityWithAttributeClass<E>(table, entityClass, *attrEntityClass)