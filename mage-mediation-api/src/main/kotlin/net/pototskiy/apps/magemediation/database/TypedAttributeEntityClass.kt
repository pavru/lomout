package net.pototskiy.apps.magemediation.database

import org.jetbrains.exposed.dao.IntEntityClass

abstract class TypedAttributeEntityClass<V : Comparable<V>, out E : TypedAttributeEntity<V>>(
    table: TypedAttributeTable<V>,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)