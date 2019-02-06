package net.pototskiy.apps.magemediation.api.database.source

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import kotlin.reflect.full.companionObject

abstract class DataEntityWithAttribute<E: DataEntityWithAttribute<E>>(id: EntityID<Int>): IntEntity(id) {
    fun getEntityClass(): E? {
        @Suppress("UNCHECKED_CAST")
        return this::class.companionObject as? E
    }
}
