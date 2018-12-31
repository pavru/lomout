package net.pototskiy.apps.magemediation.database

import org.jetbrains.exposed.dao.IntEntityClass
import kotlin.reflect.KClass

abstract class TypedAttributeEntityClass<V : Comparable<V>, out E : TypedAttributeEntity<V>>(
    table: TypedAttributeTable<V>,
    val valueType: KClass<V>,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)