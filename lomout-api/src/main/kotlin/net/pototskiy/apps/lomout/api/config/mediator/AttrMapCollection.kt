package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute

data class AttrMapCollection(private val maps: Map<AnyTypeAttribute, AnyTypeAttribute>) :
    Map<AnyTypeAttribute, AnyTypeAttribute> by maps
