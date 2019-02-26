package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.Type

data class MatcherEntityData(
    val entity: DbEntity,
    val origData: Map<AnyTypeAttribute, Type?>,
    val mappedData: Map<AnyTypeAttribute, Type?>
)
