package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageStockSources : VersionTable("mage_stock_source") {
    val sourceCode = varchar("source_code", 100).index()
    val sku = varchar("sku", 300).index()
    val status = bool("status")
    val quantity = double("quantity")

    init {
        uniqueIndex(sourceCode, sku)
    }
}

class MageStockSource(id: EntityID<Int>) : VersionEntity(id) {
    companion object : VersionEntityClass<MageStockSource>(MageStockSources) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity? {
            return transaction {
                MageStockSource.find {
                    ((MageStockSources.sourceCode eq data[MageStockSources.sourceCode.name] as String)
                            and (MageStockSources.sku eq data[MageStockSources.sku.name] as String))
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity {
            return transaction {
                MageStockSource.new {
                    sourceCode = data[MageStockSources.sourceCode.name] as String
                    sku = data[MageStockSources.sku.name] as String
                    status = data[MageStockSources.status.name] as Boolean
                    quantity = data[MageStockSources.quantity.name] as Double
                    createdInMedium = timestamp
                    updatedInMedium = timestamp
                    absentDays = 0
                }
            }
        }
    }

    var sourceCode by MageStockSources.sourceCode
    var sku by MageStockSources.sku
    var status by MageStockSources.status
    var quantity by MageStockSources.quantity
    override var createdInMedium by MageStockSources.createdInMedium
    override var updatedInMedium by MageStockSources.updatedInMedium
    override var absentDays by MageStockSources.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean {
        return status != data[MageStockSources.status.name]
                || quantity != data[MageStockSources.quantity.name]
    }

    override fun updateMainRecord(data: Map<String, Any?>) {
        transaction {
            status = data[MageStockSources.status.name] as Boolean
            quantity = data[MageStockSources.quantity.name] as Double
        }
    }
}
