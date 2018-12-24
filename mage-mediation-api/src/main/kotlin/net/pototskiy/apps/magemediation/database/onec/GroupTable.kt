package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class GroupTable(name: String): SourceDataTable(name) {
    val groupCode = varchar("group_code", 300).uniqueIndex()
    val groupName = varchar("group_name", 1000).index()
}