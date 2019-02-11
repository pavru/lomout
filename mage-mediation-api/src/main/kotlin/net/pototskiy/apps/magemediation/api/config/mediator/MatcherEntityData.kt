package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity

data class MatcherEntityData(
    val entity: PersistentSourceEntity,
    val origData: Map<Attribute, Any?>,
    val mappedData: Map<Attribute, Any?>
)
