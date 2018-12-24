package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class CategoryDouble(id: EntityID<Int>): TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, CategoryDouble>(CategoryDoubleTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryDoubleTable.owner
    override var index by CategoryDoubleTable.index
    override var code by CategoryDoubleTable.code
    override var value by CategoryDoubleTable.value
}