package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.entity.type.Type

/**
 * Attributes collection
 *
 * @property attributes List<Attribute<out Type>>
 * @constructor
 */
data class AttributeCollection(private val attributes: List<Attribute<out Type>>) :
    List<Attribute<out Type>> by attributes {
    private val nameIndex = attributes.map { it.name to it }.toMap()

    operator fun get(name: String): Attribute<*>? = nameIndex[name]
}
