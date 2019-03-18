package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.IntEntityClass

abstract class AttributeEntityClass<V : Comparable<V>, out E : AttributeEntity<V>>(
    table: AttributeTable<V>,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)
