package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.IMPORT_DATETIME
import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageProducts : ProductTable("mage_product")


class MageProduct(id: EntityID<Int>) : ProductEntity(id) {
    companion object : ProductEntityClass(MageProducts) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity? = transaction {
            MageProduct
                .find { MageProducts.sku eq (data[MageProducts.sku.name] as String) }
                .firstOrNull()
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): SourceDataEntity {
            return transaction {
                this@Companion.new {
                    sku = (data[MageProducts.sku.name] as String)
                    createdInMedium = IMPORT_DATETIME
                    updatedInMedium = IMPORT_DATETIME
                    absentDays = 0
                }
            }
        }
    }

    override var sku by MageProducts.sku
    override var createdInMedium by MageProducts.createdInMedium
    override var updatedInMedium by MageProducts.updatedInMedium
    override var absentDays by MageProducts.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean = false

    override fun updateMainRecord(data: Map<String, Any?>) {
        // all fields are key and therefore can not be updated
    }
}