package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.database.PersistentMediumEntity
import net.pototskiy.apps.magemediation.api.database.PersistentMediumEntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentMediumEntityTable
import org.jetbrains.exposed.dao.EntityID

object MediumEntities: PersistentMediumEntityTable("medium")

class MediumEntity(id: EntityID<Int>): PersistentMediumEntity(id) {
    companion object : PersistentMediumEntityClass(
        MediumEntities,
        null,
        MediumVarchar,
        MediumLong,
        MediumDouble,
        MediumBoolean,
        MediumDate,
        MediumDateTime,
        MediumText
    )
}
