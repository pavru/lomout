package net.pototskiy.apps.magemediation.database.mediation.category

import net.pototskiy.apps.magemediation.api.database.mdtn.category.MCategoryEntity
import net.pototskiy.apps.magemediation.api.database.mdtn.category.MCategoryEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.attribute.*
import org.jetbrains.exposed.dao.EntityID

class MediumCategory(id: EntityID<Int>) : MCategoryEntity(id) {
    companion object : MCategoryEntityClass(
        MediumCategories,
        null,
        MCategoryBoolAttribute.Companion,
        MCategoryDateAttribute.Companion,
        MCategoryDateTimeAttribute.Companion,
        MCategoryDoubleAttribute.Companion,
        MCategoryIntAttribute.Companion,
        MCategoryTextAttribute.Companion,
        MCategoryVarcharAttribute.Companion
    )
}