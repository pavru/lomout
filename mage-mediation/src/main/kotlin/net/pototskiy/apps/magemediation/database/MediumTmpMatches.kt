package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.ENTITY_TYPE_NAME_LENGTH
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object MediumTmpMatches : IntIdTable("medium_tmp_matches") {
    val target = varchar("target", ENTITY_TYPE_NAME_LENGTH)
    val matchedEntity = reference("matched_entity", SourceEntities, ReferenceOption.CASCADE)

    init {
        uniqueIndex(target, matchedEntity)
    }
}

class MediumTmpMatch(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MediumTmpMatch>(MediumTmpMatches)

    var target by MediumTmpMatches.target
    var matchedEntity by MediumTmpMatches.matchedEntity
}
