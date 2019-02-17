package net.pototskiy.apps.magemediation.api.database.schema

import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntityTable
import org.jetbrains.exposed.dao.EntityID

object SourceEntities: PersistentSourceEntityTable("source")

class SourceEntity(id: EntityID<Int>): PersistentSourceEntity(id) {
    companion object : PersistentSourceEntityClass(
        SourceEntities,
        null,
        SourceVarchar,
        SourceLong,
        SourceDouble,
        SourceBoolean,
        SourceDate,
        SourceDateTime,
        SourceText
    )
}
