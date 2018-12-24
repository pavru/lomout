package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategories
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MediCatVarchars : TypedAttributeTable<String>("medi_cat_varchar") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = varchar("value", 300)
}

class MediCatVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MediCatVarchar>(MediCatVarchars)

    override var owner: IntEntity by MediCategory referencedOn MediCatVarchars.owner
    override var index by MediCatVarchars.index
    override var code by MediCatVarchars.code
    override var value by MediCatVarchars.value
}