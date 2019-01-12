package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.api.database.mage.CustomerGroupEntity
import net.pototskiy.apps.magemediation.api.database.mage.CustomerGroupEntityClass
import net.pototskiy.apps.magemediation.api.database.mage.CustomerGroupTable
import org.jetbrains.exposed.dao.EntityID

object MageCustomerGroups : CustomerGroupTable("mage_customer_group")

class MageCustomerGroup(id: EntityID<Int>) : CustomerGroupEntity(id) {
    companion object : CustomerGroupEntityClass(MageCustomerGroups)

    override var customerGroupID by MageCustomerGroups.customerGroupID
    override var customerGroupCode by MageCustomerGroups.customerGroupCode
    override var taxClassID by MageCustomerGroups.taxClassID
}