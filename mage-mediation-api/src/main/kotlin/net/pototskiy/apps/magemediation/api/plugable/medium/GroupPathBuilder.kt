package net.pototskiy.apps.magemediation.api.plugable.medium

import net.pototskiy.apps.magemediation.api.database.onec.GroupEntity
import net.pototskiy.apps.magemediation.api.database.onec.GroupRelationEntityClass
import net.pototskiy.apps.magemediation.api.plugable.Plugable

interface GroupPathBuilder : Plugable {
    fun setGroupEntities(entities: GroupRelationEntityClass)
    fun buildPath(entity: GroupEntity): String
}