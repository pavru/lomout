package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object OnecProducts : ProductTable("onec_product")

class OnecProduct(id: EntityID<Int>) : ProductEntity(id) {
    companion object : ProductEntityClass(OnecProducts) {

        override fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity? {
            return transaction {
                OnecProduct.find {
                    (OnecProducts.sku eq (data[OnecProducts.sku.name] as String))
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): SourceDataEntity {
            return transaction {
                this@Companion.new {
                    sku = (data[OnecProducts.sku.name] as String)
                    createdInMedium = timestamp
                    updatedInMedium = timestamp
                    absentDays = 0
                }
            }
        }
    }

    override var sku by OnecProducts.sku
    override var createdInMedium by OnecProducts.createdInMedium
    override var updatedInMedium by OnecProducts.updatedInMedium
    override var absentDays by OnecProducts.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean = false

    override fun updateMainRecord(data: Map<String, Any?>) {
        // This entity contains only key field and therefore it can not be updated
    }
}