package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.IntEntityClass

/**
 * Exposed attribute entity class
 *
 * @param V : Comparable<V> The attribute type
 * @param E : AttributeEntity<V> The exposed attribute entity
 * @constructor
 * @param table AttributeTable<V> The attribute table
 * @param entityClass The exposed attribute entity class
 */
abstract class AttributeEntityClass<V : Comparable<V>, out E : AttributeEntity<V>>(
    table: AttributeTable<V>,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)
