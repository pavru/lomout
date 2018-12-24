package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategories
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MediCatInts : TypedAttributeTable<Long>("medi_cat_int") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = long("value")
}

class MediCatInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, MediCatInt>(MediCatInts)

    override var owner: IntEntity by MediCategory referencedOn MediCatInts.owner
    override var index by MediCatInts.index
    override var code by MediCatInts.code
    override var value by MediCatInts.value
}