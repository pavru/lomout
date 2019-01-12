package net.pototskiy.apps.magemediation.api.database.onec

import net.pototskiy.apps.magemediation.api.database.getDelegate
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

abstract class GroupRelationEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var groupCode: String
    abstract var groupParentCode: String?
    abstract var groupName: String

    @Suppress("UNCHECKED_CAST")
    final override fun isNotEqual(data: Map<String, Any?>): Boolean {
        val groupParentCodeColumn = getDelegate(
            this,
            ::groupParentCode
        ) as Column<String?>
        val groupNameColumn = getDelegate(this, ::groupName) as Column<String>
        return groupName != data[groupNameColumn.name] as String
                || groupParentCode != data[groupParentCodeColumn.name] as String
    }

    @Suppress("UNCHECKED_CAST")
    final override fun updateEntity(data: Map<String, Any?>) {
        val groupParentCodeColumn = getDelegate(
            this,
            ::groupParentCode
        ) as Column<String?>
        val groupNameColumn = getDelegate(this, ::groupName) as Column<String>
        transaction {
            groupParentCode = data[groupParentCodeColumn.name] as String
            groupName = data[groupNameColumn.name] as String
        }
        wasUpdated()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun setEntityData(data: Map<String, Any?>) {
        val groupCodeColumn = getDelegate(this, ::groupCode) as Column<String>
        val groupCodeParentColumn = getDelegate(
            this,
            ::groupParentCode
        ) as Column<String?>
        val groupNameColumn = getDelegate(this, ::groupName) as Column<String>
        groupCode = data[groupCodeColumn.name] as String
        groupParentCode = data[groupCodeParentColumn.name] as String?
        groupName = data[groupNameColumn.name] as String
    }
}
