package net.pototskiy.apps.magemediation.database.mediation.category

import net.pototskiy.apps.magemediation.database.mdtn.MediumDataEntityClass
import net.pototskiy.apps.magemediation.database.mdtn.category.CategoryEntity
import org.jetbrains.exposed.dao.EntityID

class MediumCategory(id: EntityID<Int>) : CategoryEntity(id) {
    companion object : MediumDataEntityClass<MediumCategory>(MediumCategories)

    override var entityID by MediumCategories.entityID
}