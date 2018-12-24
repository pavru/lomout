package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class CategoryVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, CategoryVarchar>(CategoryVarcharTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryVarcharTable.owner
    override var index by CategoryVarcharTable.index
    override var code by CategoryVarcharTable.code
    override var value by CategoryVarcharTable.value
}