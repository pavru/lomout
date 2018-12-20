package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.IMPORT_DATETIME
import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import net.pototskiy.apps.magemediation.importer.cctu
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageProducts : VersionTable("mage_product") {
    val sku = varchar(cctu("sku"), 100).uniqueIndex()

    override fun findRecordByKeyFields(data: Map<String, Any?>): VersionEntity? {
        return transaction {
            MageProduct.find { MageProducts.sku eq (data[MageProducts.sku.name] as String) }
                .firstOrNull()
        }
    }

    override fun insertNewRecord(data: Map<String, Any?>): VersionEntity {
        return transaction {
            MageProduct.new {
                sku = (data[this@MageProducts.sku.name] as String)
                createdInMedium = IMPORT_DATETIME
                updatedInMedium = IMPORT_DATETIME
                absentDays = 0
            }
        }
    }

    override fun mainDataIsEqual(current: VersionEntity, data: Map<String, Any?>): Boolean {
        return false
    }

    override fun updateMainRecord(current: VersionEntity, data: Map<String, Any?>) {
        return
    }
}


class MageProduct(id: EntityID<Int>) : VersionEntity(id) {
    companion object : VersionEntityClass<MageProduct>(MageProducts) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity? = transaction {
            MageProduct
                .find { MageProducts.sku eq (data[MageProducts.sku.name] as String) }
                .firstOrNull()
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity {
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

    var sku by MageProducts.sku
    override var createdInMedium by MageProducts.createdInMedium
    override var updatedInMedium by MageProducts.updatedInMedium
    override var absentDays by MageProducts.absentDays

    override fun mainDataIsEqual(data: Map<String, Any?>): Boolean = false

    override fun updateMainRecord(data: Map<String, Any?>) {}
}