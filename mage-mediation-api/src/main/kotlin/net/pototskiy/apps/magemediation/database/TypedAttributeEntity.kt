package net.pototskiy.apps.magemediation.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

abstract class TypedAttributeEntity<V : Comparable<V>>(id: EntityID<Int>) : IntEntity(id) {
    abstract var owner: IntEntity
    abstract var index: Int
    abstract var code: String
    abstract var value: V

    fun compareToWithTypeCheck(other: Any): Int {
        @Suppress("UNCHECKED_CAST")
        return value.compareTo(other as V)
    }

    fun setValueWithTypeCheck(value: Any) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as V
    }
}