package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.database.newschema.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.newschema.PersistentSourceEntityClass
import net.pototskiy.apps.magemediation.api.database.newschema.PersistentSourceEntityTable
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
