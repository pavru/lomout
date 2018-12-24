package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class CategoryInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, CategoryInt>(CategoryIntTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryIntTable.owner
    override var index by CategoryIntTable.index
    override var code by CategoryIntTable.code
    override var value by CategoryIntTable.value
}