package net.pototskiy.apps.magemediation.database.mediation

import org.jetbrains.exposed.dao.IntEntityClass

abstract class MediEntityClass<out E : MediEntity>(
    table: MediTable,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)