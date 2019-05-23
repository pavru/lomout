package net.pototskiy.apps.lomout.api.database

import org.jetbrains.exposed.dao.IntEntityClass

/**
 * Exposed attribute entity class
 *
 * @param V The attribute type
 * @param E The exposed attribute entity
 * @constructor
 * @param table The attribute table
 * @param entityClass The exposed attribute entity class
 */
abstract class AttributeEntityClass<V : Comparable<V>, out E : AttributeEntity<V>>(
    table: AttributeTable<V>,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)
