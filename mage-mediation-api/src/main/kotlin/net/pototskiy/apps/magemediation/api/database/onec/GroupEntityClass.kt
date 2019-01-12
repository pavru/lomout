package net.pototskiy.apps.magemediation.api.database.onec

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

abstract class GroupEntityClass(
    table: GroupTable,
    entityClass: Class<GroupEntity>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : SourceDataEntityClass<GroupEntity>(table, entityClass, *attrEntityClass) {

    override fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean> {
        table as GroupTable
        return table.groupCode eq data[table.groupCode.name] as String
    }
}