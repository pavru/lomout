package net.pototskiy.apps.magemediation.api.database.onec

import net.pototskiy.apps.magemediation.api.database.getDelegate
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

abstract class GroupEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var groupCode: String
    abstract var groupName: String

    @Suppress("UNCHECKED_CAST")
    final override fun isNotEqual(data: Map<String, Any?>): Boolean {
        val groupNameColumn = getDelegate(this, ::groupName) as Column<String>
        return groupName != data[groupNameColumn.name] as String
    }

    @Suppress("UNCHECKED_CAST")
    final override fun updateEntity(data: Map<String, Any?>) {
        val groupNameColumn = getDelegate(this, ::groupName) as Column<String>
        transaction {
            groupName = data[groupNameColumn.name] as String
        }
        wasUpdated()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun setEntityData(data: Map<String, Any?>) {
        val groupCodeColumn = getDelegate(this, ::groupCode) as Column<String>
        val groupNameColumn = getDelegate(this, ::groupName) as Column<String>
        groupCode = data[groupCodeColumn.name] as String
        groupName = data[groupNameColumn.name] as String
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroupEntity) return false

        if (groupCode != other.groupCode) return false
        if (groupName != other.groupName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupCode.hashCode()
        result = 31 * result + groupName.hashCode()
        return result
    }

    override fun toString(): String {
        return "GroupEntity(groupCode='$groupCode', groupName='$groupName')"
    }
}
