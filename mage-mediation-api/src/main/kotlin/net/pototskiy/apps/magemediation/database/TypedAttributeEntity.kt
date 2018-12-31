package net.pototskiy.apps.magemediation.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

abstract class TypedAttributeEntity<V : Comparable<V>>(id: EntityID<Int>) : IntEntity(id) {
    var owner: EntityID<Int>
        set(value) = (klass.table as TypedAttributeTable<V>).owner.setValue(this, ::owner, value)
        get() = (klass.table as TypedAttributeTable<V>).owner.getValue(this, ::owner)
    var index: Int
        set(value) = (klass.table as TypedAttributeTable<V>).index.setValue(this, ::index, value)
        get() = (klass.table as TypedAttributeTable<V>).index.getValue(this, ::index)
    var code: String
        set(value) = (klass.table as TypedAttributeTable<V>).code.setValue(this, ::code, value)
        get() = (klass.table as TypedAttributeTable<V>).code.getValue(this, ::code)
    var value: V
        set(value) = (klass.table as TypedAttributeTable<V>).value.setValue(this, ::value, value)
        get() = (klass.table as TypedAttributeTable<V>).value.getValue(this, ::value)

    fun compareToWithTypeCheck(other: Any): Int {
        @Suppress("UNCHECKED_CAST")
        return value.compareTo(other as V)
    }

    fun setValueWithTypeCheck(value: Any) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as V
    }
}