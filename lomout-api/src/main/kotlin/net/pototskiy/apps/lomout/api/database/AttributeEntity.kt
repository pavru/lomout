package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.entity.Type
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import kotlin.reflect.KClass

/**
 * Exposed entity to save attribute values
 *
 * @param V The attribute value type
 * @constructor
 */
abstract class AttributeEntity<V : Comparable<V>>(id: EntityID<Int>) : IntEntity(id) {
    /**
     * The attribute owner id
     */
    @Suppress("UNCHECKED_CAST")
    var owner: EntityID<Int>
        set(value) = (klass.table as AttributeTable<V>).owner.setValue(this, ::owner, value)
        get() = (klass.table as AttributeTable<V>).owner.getValue(this, ::owner)
    /**
     * Index of value in list type attribute, -1 — not list type
     */
    @Suppress("UNCHECKED_CAST")
    var index: Int
        set(value) = (klass.table as AttributeTable<V>).index.setValue(this, ::index, value)
        get() = (klass.table as AttributeTable<V>).index.getValue(this, ::index)
    /**
     * Attribute code (name)
     */
    @Suppress("UNCHECKED_CAST")
    var code: String
        set(value) = (klass.table as AttributeTable<V>).code.setValue(this, ::code, value)
        get() = (klass.table as AttributeTable<V>).code.getValue(this, ::code)
    /**
     * Attribute value
     */
    @Suppress("UNCHECKED_CAST")
    var value: V
        set(value) = (klass.table as AttributeTable<V>).value.setValue(this, ::value, value)
        get() = (klass.table as AttributeTable<V>).value.getValue(this, ::value)

    /**
     * Compare attribute with type check
     *
     * @param other Any Other attribute
     * @return Int
     */
    fun compareToWithTypeCheck(other: Any): Int {
        @Suppress("UNCHECKED_CAST")
        return value.compareTo(other as V)
    }

    /**
     * Set attribute value with type check
     *
     * @param value Type The value to set
     */
    fun setValueWithTypeCheck(value: Type) {
        val klass = this::class.supertypes[0].arguments[0].type?.classifier
        if (!value.isSingle() || (klass is KClass<*> && !klass.isInstance(value.value))) {
            throw AppDataException("Value cannot be assigned to attribute, types are incompatible")
        }
        @Suppress("UNCHECKED_CAST")
        this.value = value.value as V
    }
}
