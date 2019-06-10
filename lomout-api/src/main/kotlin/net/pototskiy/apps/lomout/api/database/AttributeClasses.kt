package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.isTypeOf
import net.pototskiy.apps.lomout.api.entity.sqlType
import org.cache2k.Cache2kBuilder

/**
 * Retrieve attribute classes (tables) to download entity attributes.
 *
 */
class AttributeClasses(private val classes: List<AttributeEntityClass<*, *>>) {
    private val cache = Cache2kBuilder.of(Key::class.java, List::class.java)
        .name("typeAttrClasses")
        .entryCapacity(MAX_CACHE_SIZE)
        .eternal(true)
        .loader { filterAttrClasses(it.type) }
        .enableJmx(true)
        .build()

    /**
     * Get entity type attribute classes
     *
     * @param type The entity type
     * @return List<AttributeEntityClass<*, *>>
     */
    @Suppress("UNCHECKED_CAST")
    fun getAttrClasses(type: EntityType): List<AttributeEntityClass<*, *>> =
        cache.get(Key(type)) as List<AttributeEntityClass<*, *>>

    /**
     * Find entity type attribute classes
     *
     * @param type The entity type
     * @return List<AttributeEntityClass<*, *>>
     */
    private fun filterAttrClasses(type: EntityType): List<AttributeEntityClass<*, *>> {
        val types = type.attributes
            .filterNot { it.isSynthetic || it.valueType.isTypeOf<AttributeListType>() }
            .groupBy { it.valueType.sqlType() }.keys
        return classes.filter {
            (it.table as AttributeTable<*>).value.columnType::class in types
        }
    }

    /**
     * Entity type key
     *
     * @property type The entity type
     * @property hash The entity type hash code
     * @constructor
     */
    data class Key(val type: EntityType, val hash: Int = type.attributes.hashCode())

    companion object {
        private const val MAX_CACHE_SIZE = 100L
    }
}
