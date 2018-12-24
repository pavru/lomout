package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategories
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MediCatDoubles: TypedAttributeTable<Double>("medi_cat_double") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = double("value")
}

class MediCatDouble(id: EntityID<Int>): TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, MediCatDouble>(MediCatDoubles)

    override var owner: IntEntity by MediCategory referencedOn MediCatDoubles.owner
    override var index by MediCatDoubles.index
    override var code by MediCatDoubles.code
    override var value by MediCatDoubles.value
}