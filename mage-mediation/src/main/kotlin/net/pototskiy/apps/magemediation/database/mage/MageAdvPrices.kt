package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageAdvPrices : VersionTable("mage_adv_price") {
    val sku = varchar("sku", 300).index()
    val tierPriceWebsite = varchar("tier_price_website", 200).index()
    val tierPriceCustomerGroup = varchar("tier_price_customer_group", 200).index()
    val tierPriceQty = double("tier_price_qty")
    val tierPrice = double("tier_price")
    val tierPriceValueType = varchar("tier_price_value_type", 200)

    init {
        uniqueIndex(sku, tierPriceWebsite, tierPriceCustomerGroup)
    }
}

class MageAdvPrice(id: EntityID<Int>) : VersionEntity(id) {
    companion object : VersionEntityClass<MageAdvPrice>(MageAdvPrices) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity? {
            return transaction {
                MageAdvPrice.find {
                    ((MageAdvPrices.sku eq data[MageAdvPrices.sku.name] as String)
                            and (MageAdvPrices.tierPriceWebsite eq data[MageAdvPrices.tierPriceValueType.name] as String)
                            and (MageAdvPrices.tierPriceCustomerGroup eq data[MageAdvPrices.tierPriceCustomerGroup.name] as String))
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity {
            return transaction {
                MageAdvPrice.new {
                    sku = data[MageAdvPrices.sku.name] as String
                    tierPriceWebsite = data[MageAdvPrices.tierPriceWebsite.name] as String
                    tierPriceCustomerGroup = data[MageAdvPrices.tierPriceCustomerGroup.name] as String
                    tierPriceQty = data[MageAdvPrices.tierPriceQty.name] as Double
                    tierPrice = data[MageAdvPrices.tierPrice.name] as Double
                    tierPriceValueType = data[MageAdvPrices.tierPriceValueType.name] as String

                    createdInMedium = timestamp
                    updatedInMedium = timestamp
                    absentDays = 0
                }
            }
        }
    }

    var sku by MageAdvPrices.sku
    var tierPriceWebsite by MageAdvPrices.tierPriceWebsite
    var tierPriceCustomerGroup by MageAdvPrices.tierPriceCustomerGroup
    var tierPriceQty by MageAdvPrices.tierPriceQty
    var tierPrice by MageAdvPrices.tierPrice
    var tierPriceValueType by MageAdvPrices.tierPriceValueType
    override var createdInMedium by MageAdvPrices.createdInMedium
    override var updatedInMedium by MageAdvPrices.updatedInMedium
    override var absentDays by MageAdvPrices.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean =
        (tierPriceQty != data[MageAdvPrices.tierPriceQty.name]
                || tierPrice != data[MageAdvPrices.tierPrice.name]
                || tierPriceValueType != data[MageAdvPrices.tierPriceValueType.name])

    override fun updateMainRecord(data: Map<String, Any?>) {
        transaction {
            tierPriceQty = data[MageAdvPrices.tierPriceQty.name] as Double
            tierPrice = data[MageAdvPrices.tierPrice.name] as Double
            tierPriceValueType = data[MageAdvPrices.tierPriceValueType.name] as String
        }
    }
}