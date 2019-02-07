package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import kotlin.reflect.full.companionObject

abstract class PersistentEntity<E : PersistentEntity<E>>(id: EntityID<Int>) : IntEntity(id) {
    var entityType: String
        get() = (klass.table as PersistentEntityTable).entityType.getValue(this, ::entityType)
        set(value) = (klass.table as PersistentEntityTable).entityType.setValue(this, ::entityType, value)

    var data: MutableMap<Attribute, Any?> = mutableMapOf()

    fun removeAttribute(attribute: Attribute) =
        (klass as PersistentEntityClass<*>).removeAttribute(this, attribute)

    fun addAttribute(attribute: Attribute, value: Any) =
        (klass as PersistentEntityClass<*>).addAttribute(this, attribute, value)

    fun updateAttribute(attribute: Attribute, value: Any) =
        (klass as PersistentEntityClass<*>).updateAttribute(this, attribute, value)

    fun readAttribute(attribute: Attribute): Any? =
        (klass as PersistentEntityClass<*>).readAttribute(this, attribute)

    fun readsAttributes(): Map<Attribute, Any?> =
        (klass as PersistentEntityClass<*>).readAttributes(this)

    fun getPersistentEntityClass(): E? {
        @Suppress("UNCHECKED_CAST")
        return this::class.companionObject as? E
    }

    fun getEntityClass(): EntityClass<E> {
        @Suppress("UNCHECKED_CAST")
        return EntityClass.getClass(entityType) as? EntityClass<E>
            ?: throw DatabaseException("Entity<$entityType> is not defined")
    }
}
