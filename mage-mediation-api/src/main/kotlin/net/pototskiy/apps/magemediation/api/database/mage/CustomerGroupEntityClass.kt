package net.pototskiy.apps.magemediation.api.database.mage

import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

abstract class CustomerGroupEntityClass(
    table: CustomerGroupTable,
    entityClass: Class<CustomerGroupEntity>? = null
) : SourceDataEntityClass<CustomerGroupEntity>(table, entityClass) {

    override fun SqlExpressionBuilder.keyWhereExpression(data: Map<String, Any?>): Op<Boolean> {
        table as CustomerGroupTable
        return table.customerGroupID eq data[table.customerGroupID.name] as Long
    }
}