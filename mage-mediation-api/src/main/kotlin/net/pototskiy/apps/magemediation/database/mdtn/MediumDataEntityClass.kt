package net.pototskiy.apps.magemediation.database.mdtn

import org.jetbrains.exposed.dao.IntEntityClass

abstract class MediumDataEntityClass<out E : MediumDataEntity>(
    table: MediumDataTable,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)