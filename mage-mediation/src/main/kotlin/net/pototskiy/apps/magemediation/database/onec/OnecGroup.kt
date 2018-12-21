package net.pototskiy.apps.magemediation.database.onec

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object OnecGroups : VersionTable("onec_group") {
    val groupCode = varchar("group_code", 300).uniqueIndex()
    val groupName = varchar("group_name", 1000).index()
}

class OnecGroup(id: EntityID<Int>) : VersionEntity(id) {
    companion object : VersionEntityClass<OnecGroup>(OnecGroups) {
        override fun findEntityByKeyFields(data: Map<String, Any?>): VersionEntity? =
            transaction {
                OnecGroup.find {
                    OnecGroups.groupCode eq (data[OnecGroups.groupCode.name] as String)
                }.firstOrNull()
            }

        override fun insertNewRecord(data: Map<String, Any?>, timestamp: DateTime): VersionEntity =
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

    var groupCode by OnecGroups.groupCode
    var groupName by OnecGroups.groupName
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
