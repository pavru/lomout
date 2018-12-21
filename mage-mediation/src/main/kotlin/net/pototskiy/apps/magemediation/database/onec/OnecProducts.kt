package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import net.pototskiy.apps.magemediation.cctu
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object OnecProducts : VersionTable("onec_product") {
    val sku = varchar(cctu("sku"), 100).uniqueIndex()

//    override fun findRecordByKeyFields(data: Map<String, Any?>): VersionEntity? {
//        return transaction {
//            OnecProduct.find { OnecProducts.sku eq (data[OnecProducts.sku.name] as String) }
//                .firstOrNull()
//        }
//    }
//
//    override fun insertNewRecord(data: Map<String, Any?>): VersionEntity {
//        return transaction {
//            OnecProduct.new {
//                sku = (data[this@OnecProducts.sku.name] as String)
//                createdInMedium = IMPORT_DATETIME
//                updatedInMedium = IMPORT_DATETIME
//                absentDays = 0
//            }
//        }
//    }
//
//    override fun mainDataIsNotEqual(current: VersionEntity, data: Map<String, Any?>): Boolean = false
//
//    override fun updateMainRecord(current: VersionEntity, data: Map<String, Any?>) {
//        // All fields are key and therefore can not be updated
//    }
}


class OnecProduct(id: EntityID<Int>) : VersionEntity(id) {
    companion object : VersionEntityClass<OnecProduct>(OnecProducts) {

        override fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity? {
            return transaction {
                OnecProduct.find {
                    (OnecProducts.sku eq (data[OnecProducts.sku.name] as String))
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity {
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

    var sku by OnecProducts.sku
    override var createdInMedium by OnecProducts.createdInMedium
    override var updatedInMedium by OnecProducts.updatedInMedium
    override var absentDays by OnecProducts.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean = false

    override fun updateMainRecord(data: Map<String, Any?>) {
        // This entity contains only key field and therefore it can not be updated
    }
}