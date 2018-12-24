package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageAdvPrices : AdvPriceTable("mage_adv_price")

class MageAdvPrice(id: EntityID<Int>) : AdvPriceEntity(id) {
    companion object : AdvPriceEntityClass(MageAdvPrices) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity? {
            return transaction {
                MageAdvPrice.find {
                    ((MageAdvPrices.sku eq data[MageAdvPrices.sku.name] as String)
                            and (MageAdvPrices.tierPriceWebsite eq data[MageAdvPrices.tierPriceValueType.name] as String)
                            and (MageAdvPrices.tierPriceCustomerGroup eq data[MageAdvPrices.tierPriceCustomerGroup.name] as String))
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): SourceDataEntity {
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

    override var sku by MageAdvPrices.sku
    override var tierPriceWebsite by MageAdvPrices.tierPriceWebsite
    override var tierPriceCustomerGroup by MageAdvPrices.tierPriceCustomerGroup
    override var tierPriceQty by MageAdvPrices.tierPriceQty
    override var tierPrice by MageAdvPrices.tierPrice
    override var tierPriceValueType by MageAdvPrices.tierPriceValueType
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