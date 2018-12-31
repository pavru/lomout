package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

abstract class CategoryEntityClass(
    table: CategoryTable,
    entityClass: Class<CategoryEntity>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : SourceDataEntityClass<CategoryEntity>(table, entityClass, *attrEntityClass) {

    final override fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean> {
        table as CategoryTable
        return table.entityID eq data[table.entityID.name] as Long
    }
}