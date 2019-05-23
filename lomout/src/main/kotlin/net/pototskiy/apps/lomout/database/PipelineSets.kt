package net.pototskiy.apps.lomout.database

import net.pototskiy.apps.lomout.api.database.DbEntityTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

private const val UUID_LENGTH = 50

object PipelineSets : IntIdTable("pipeline_set") {

    val setID = varchar("set_id", UUID_LENGTH)
    val entityID = reference("entity_id", DbEntityTable, ReferenceOption.CASCADE)
    val isMatched = bool("is_matched").default(false).index()

    init {
        uniqueIndex(setID, entityID)
    }
}

@Suppress("unused")
class PipelineSet(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PipelineSet>(PipelineSets)

    var setID by PipelineSets.setID
    var entityID by PipelineSets.entityID
    var isMatched by PipelineSets.isMatched
}
