package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and

abstract class StockSourceEntityClass(
    table: StockSourceTable,
    entityClass: Class<StockSourceEntity>? = null
) : SourceDataEntityClass<StockSourceEntity>(table, entityClass) {

    final override fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean> {
        table as StockSourceTable
        return ((table.sourceCode eq data[table.sourceCode.name] as String)
                and (table.sku eq data[table.sku.name] as String))
    }
}