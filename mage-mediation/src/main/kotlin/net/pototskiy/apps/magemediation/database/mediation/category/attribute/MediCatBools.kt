package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategories
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MediCatBools : TypedAttributeTable<Boolean>("medium_cat_bool") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = bool("value")
}

class MediCatBool(id: EntityID<Int>) : TypedAttributeEntity<Boolean>(id) {
    companion object : TypedAttributeEntityClass<Boolean, MediCatBool>(MediCatBools)

    override var owner: IntEntity by MediCategory referencedOn MediCatBools.owner
    override var index by MediCatBools.index
    override var code by MediCatBools.code
    override var value by MediCatBools.value
}