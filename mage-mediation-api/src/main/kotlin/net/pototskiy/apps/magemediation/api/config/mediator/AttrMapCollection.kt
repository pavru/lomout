package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.data.Attribute

data class AttrMapCollection(private val maps: Map<Attribute, AttrMap>) : Map<Attribute, AttrMap> by maps
