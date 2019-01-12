package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.api.database.onec.GroupRelationEntity
import net.pototskiy.apps.magemediation.api.database.onec.GroupRelationEntityClass
import net.pototskiy.apps.magemediation.api.database.onec.GroupRelationTable
import org.jetbrains.exposed.dao.EntityID

object OnecGroupRelations : GroupRelationTable("onec_group_relation")

class OnecGroupRelation(id: EntityID<Int>) : GroupRelationEntity(id) {
    companion object : GroupRelationEntityClass(OnecGroupRelations)

    override var groupCode by OnecGroupRelations.groupCode
    override var groupParentCode by OnecGroupRelations.groupParentCode
    override var groupName by OnecGroupRelations.groupName
}
