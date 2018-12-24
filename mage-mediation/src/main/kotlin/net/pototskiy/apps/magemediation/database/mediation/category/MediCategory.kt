package net.pototskiy.apps.magemediation.database.mediation.category

import net.pototskiy.apps.magemediation.database.mediation.MediEntity
import net.pototskiy.apps.magemediation.database.mediation.MediEntityClass
import org.jetbrains.exposed.dao.EntityID

class MediCategory(id: EntityID<Int>) : MediEntity(id) {
    companion object : MediEntityClass<MediCategory>(MediCategories)

    var entityID by MediCategories.entityID
}