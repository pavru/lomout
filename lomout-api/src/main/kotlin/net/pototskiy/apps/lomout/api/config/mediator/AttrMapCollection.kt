package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute

/**
 * Entity attribute map attr->attr
 *
 * @property maps Map<Attribute<out Type>, Attribute<out Type>>
 * @constructor
 */
data class AttrMapCollection(private val maps: Map<AnyTypeAttribute, AnyTypeAttribute>) :
    Map<AnyTypeAttribute, AnyTypeAttribute> by maps
