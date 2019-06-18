package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.entity.type.Type

/**
 * Attributes collection
 *
 * @property attributes List<Attribute<out Type>>
 * @constructor
 */
data class AttributeCollection(private val attributes: List<Attribute<out Type>>) :
    List<Attribute<out Type>> by attributes
