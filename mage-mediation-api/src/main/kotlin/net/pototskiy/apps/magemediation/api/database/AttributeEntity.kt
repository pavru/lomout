package net.pototskiy.apps.magemediation.api.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import kotlin.reflect.KClass

abstract class AttributeEntity<V : Comparable<V>>(id: EntityID<Int>) : IntEntity(id) {

    @Suppress("UNCHECKED_CAST")
    var owner: EntityID<Int>
        set(value) = (klass.table as AttributeTable<V>).owner.setValue(this, ::owner, value)
        get() = (klass.table as AttributeTable<V>).owner.getValue(this, ::owner)
    @Suppress("UNCHECKED_CAST")
    var index: Int
        set(value) = (klass.table as AttributeTable<V>).index.setValue(this, ::index, value)
        get() = (klass.table as AttributeTable<V>).index.getValue(this, ::index)
    @Suppress("UNCHECKED_CAST")
    var code: String
        set(value) = (klass.table as AttributeTable<V>).code.setValue(this, ::code, value)
        get() = (klass.table as AttributeTable<V>).code.getValue(this, ::code)
    @Suppress("UNCHECKED_CAST")
    var value: V
        set(value) = (klass.table as AttributeTable<V>).value.setValue(this, ::value, value)
        get() = (klass.table as AttributeTable<V>).value.getValue(this, ::value)

    fun compareToWithTypeCheck(other: Any): Int {
        @Suppress("UNCHECKED_CAST")
        return value.compareTo(other as V)
    }

    fun setValueWithTypeCheck(value: Any) {
        val klass = this::class.supertypes[0].arguments[0].type?.classifier
        if (klass !is KClass<*> || !klass.isInstance(value)) {
            throw DatabaseException("Value can not be assigned to attribute, types are incompatible")
        }
        @Suppress("UNCHECKED_CAST")
        this.value = value as V
    }
}
