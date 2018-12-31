package net.pototskiy.apps.magemediation.database.onec

import org.jetbrains.exposed.dao.EntityID

object OnecGroups : GroupTable("onec_group")

class OnecGroup(id: EntityID<Int>) : GroupEntity(id) {
    companion object : GroupEntityClass(OnecGroups)

    override var groupCode by OnecGroups.groupCode
    override var groupName by OnecGroups.groupName
}
