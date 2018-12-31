package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.getDelegate
import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

abstract class GroupEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var groupCode: String
    abstract var groupName: String

    @Suppress("UNCHECKED_CAST")
    final override fun isNotEqual(data: Map<String, Any?>): Boolean {
        val groupNameColumn = getDelegate(this,::groupName) as Column<String>
        return groupName != data[groupNameColumn.name] as String
    }

    @Suppress("UNCHECKED_CAST")
    final override fun updateEntity(data: Map<String, Any?>) {
        val groupNameColumn = getDelegate(this,::groupName) as Column<String>
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
}
