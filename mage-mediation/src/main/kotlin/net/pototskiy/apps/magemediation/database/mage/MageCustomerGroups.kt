package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageCustomerGroups : CustomerGroupTable("mage_customer_group")

class MageCustomerGroup(id: EntityID<Int>) : CustomerGroupEntity(id) {
    companion object : CustomerGroupEntityClass(MageCustomerGroups) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity? {
            return transaction {
                MageCustomerGroup.find {
                    MageCustomerGroups.customerGroupID eq (data[MageCustomerGroups.customerGroupID.name] as Long)
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): SourceDataEntity {
            return transaction {
                MageCustomerGroup.new {
                    createdInMedium = timestamp
                    updatedInMedium = timestamp
                    absentDays = 0
                    customerGroupID = data[MageCustomerGroups.customerGroupID.name] as Long
                    customerGroupCode = data[MageCustomerGroups.customerGroupCode.name] as String
                    taxClassID = data[MageCustomerGroups.taxClassID.name] as Long
                }
            }
        }
    }

    override var customerGroupID by MageCustomerGroups.customerGroupID
    override var customerGroupCode by MageCustomerGroups.customerGroupCode
    override var taxClassID by MageCustomerGroups.taxClassID

    override var createdInMedium by MageCustomerGroups.createdInMedium
    override var updatedInMedium by MageCustomerGroups.updatedInMedium
    override var absentDays by MageCustomerGroups.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean =
        (customerGroupCode != data[MageCustomerGroups.customerGroupCode.name] as String
                || taxClassID != data[MageCustomerGroups.taxClassID.name] as Long)

    override fun updateMainRecord(data: Map<String, Any?>) {
        transaction {
            customerGroupCode = data[MageCustomerGroups.customerGroupCode.name] as String
            taxClassID = data[MageCustomerGroups.taxClassID.name] as Long
        }
    }
}