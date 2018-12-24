package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategories
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MediCatTexts : TypedAttributeTable<String>("medi_cat_text") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = text("value")
}

class MediCatText(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MediCatText>(MediCatTexts)

    override var owner: IntEntity by MediCategory referencedOn MediCatTexts.owner
    override var index by MediCatTexts.index
    override var code by MediCatTexts.code
    override var value by MediCatTexts.value
}