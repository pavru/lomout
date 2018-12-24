package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID

abstract class GroupEntity(id:EntityID<Int>): SourceDataEntity(id) {
    abstract var groupCode: String
    abstract var groupName: String
}