package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute


data class AttrMapCollection(private val maps: Map<AnyTypeAttribute, AnyTypeAttribute>) :
    Map<AnyTypeAttribute, AnyTypeAttribute> by maps
