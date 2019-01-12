package net.pototskiy.apps.magemediation.api.database.onec

import net.pototskiy.apps.magemediation.api.database.source.SourceDataTable

abstract class GroupRelationTable(name: String): SourceDataTable(name) {
    val groupCode = varchar("group_code",50).uniqueIndex()
    val groupParentCode = varchar("group_parent_code",50).index().nullable()
    val groupName = varchar("group_name", 1000).index()
}