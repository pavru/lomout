package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass

abstract class GroupEntityClass(
    table: GroupTable,
    entityClass: Class<GroupEntity>? = null
) : SourceDataEntityClass<GroupEntity>(table, entityClass)