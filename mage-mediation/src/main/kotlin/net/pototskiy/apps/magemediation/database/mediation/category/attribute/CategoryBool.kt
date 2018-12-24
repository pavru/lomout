package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class CategoryBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, CategoryBool>(CategoryBoolTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryBoolTable.owner
    override var index by CategoryBoolTable.index
    override var code by CategoryBoolTable.code
    override var value by CategoryBoolTable.value
}
