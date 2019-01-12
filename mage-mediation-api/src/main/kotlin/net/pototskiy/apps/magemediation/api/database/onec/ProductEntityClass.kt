package net.pototskiy.apps.magemediation.api.database.onec

import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

abstract class ProductEntityClass(
    table: ProductTable,
    entityClass: Class<ProductEntity>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : SourceDataEntityClass<ProductEntity>(table, entityClass, *attrEntityClass) {

    override fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean> {
        table as ProductTable
        return table.sku eq (data[table.sku.name] as String)
    }
}