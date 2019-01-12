package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and

abstract class AdvPriceEntityClass(
    table: AdvPriceTable,
    entityClass: Class<AdvPriceEntity>? = null
) : SourceDataEntityClass<AdvPriceEntity>(table, entityClass) {

    final override fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean> {
        table as AdvPriceTable
        return ((table.sku eq data[table.sku.name] as String)
                and (table.tierPriceWebsite eq data[table.tierPriceWebsite.name] as String)
                and (table.tierPriceCustomerGroup eq data[table.tierPriceCustomerGroup.name] as String))
    }
}