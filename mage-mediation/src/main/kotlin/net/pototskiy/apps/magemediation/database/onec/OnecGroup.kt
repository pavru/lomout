package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object OnecGroups : GroupTable("onec_group")

class OnecGroup(id: EntityID<Int>) : GroupEntity(id) {
    companion object : GroupEntityClass(OnecGroups) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): SourceDataEntity? =
            transaction {
                OnecGroup.find {
                    OnecGroups.groupCode eq (data[OnecGroups.groupCode.name] as String)
                }.firstOrNull()
            }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): SourceDataEntity =
            transaction {
                this@Companion.new {
                    groupCode = data[OnecGroups.groupCode.name] as String
                    groupName = data[OnecGroups.groupName.name] as String
                    createdInMedium = timestamp
                    updatedInMedium = timestamp
                    absentDays = 0
                }
            }
    }

    override var groupCode by OnecGroups.groupCode
    override var groupName by OnecGroups.groupName
    override var createdInMedium by OnecGroups.createdInMedium
    override var updatedInMedium by OnecGroups.updatedInMedium
    override var absentDays by OnecGroups.absentDays

    override fun mainDataIsNotEqual(data: Map<String, Any?>): Boolean {
        return groupName != data[OnecGroups.groupName.name]
    }

    override fun updateMainRecord(data: Map<String, Any?>) {
        transaction {
            groupName = data[OnecGroups.groupName.name] as String
        }
    }
}
