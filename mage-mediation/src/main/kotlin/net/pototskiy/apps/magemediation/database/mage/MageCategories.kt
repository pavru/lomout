package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MageCategories : VersionTable("mage_category") {
    val entityID = long("entity_id").uniqueIndex()
}

class MageCategory(id: EntityID<Int>) : VersionEntity(id) {
    companion object : VersionEntityClass<MageCategory>(MageCategories) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity? {
            return transaction {
                MageCategory.find {
                    MageCategories.entityID eq data[MageCategories.entityID.name] as Long
                }.firstOrNull()
            }
        }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity {
            return transaction {
                MageCategory.new {
                    entityID = data[MageCategories.entityID.name] as Long
                    createdInMedium = timestamp
                    updatedInMedium = timestamp
                    absentDays = 0
                }
            }
        }
    }

    var entityID by MageCategories.entityID
    override var createdInMedium by MageCategories.createdInMedium
    override var updatedInMedium by MageCategories.updatedInMedium
    override var absentDays by MageCategories.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean = false

    override fun updateMainRecord(data: Map<String, Any?>) {
        // main table contains only key field and therefore can not be updated
    }
}