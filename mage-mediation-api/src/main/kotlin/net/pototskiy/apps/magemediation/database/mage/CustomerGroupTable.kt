package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataTable

abstract class CustomerGroupTable(name: String): SourceDataTable(name) {
    val customerGroupID = long("customer_group_id").uniqueIndex()
    val customerGroupCode = varchar("customer_group_code", 255)
    val taxClassID = long("tax_class_id")
}