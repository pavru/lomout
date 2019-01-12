package net.pototskiy.apps.magemediation.database.mediation.category

import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataEntityClass
import net.pototskiy.apps.magemediation.api.database.mdtn.category.CategoryEntity
import net.pototskiy.apps.magemediation.database.mediation.category.attribute.*
import org.jetbrains.exposed.dao.EntityID

class MediumCategory(id: EntityID<Int>) : CategoryEntity(id) {
    companion object : MediumDataEntityClass<MediumCategory>(
        MediumCategories,
        null,
        CategoryBool.Companion,
        CategoryDate.Companion,
        CategoryDateTime.Companion,
        CategoryDouble.Companion,
        CategoryInt.Companion,
        CategoryText.Companion,
        CategoryVarchar.Companion
    )

    override var entityID by MediumCategories.entityID
}