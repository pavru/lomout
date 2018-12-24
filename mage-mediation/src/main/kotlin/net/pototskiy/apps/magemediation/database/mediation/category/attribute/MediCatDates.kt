package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategories
import net.pototskiy.apps.magemediation.database.mediation.category.MediCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object MediCatDates : TypedAttributeTable<DateTime>("medi_cat_bool") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = date("value")
}

class MediCatDate(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MediCatDate>(MediCatDates)

    override var owner: IntEntity by MediCategory referencedOn MediCatDates.owner
    override var index by MediCatDates.index
    override var code by MediCatDates.code
    override var value by MediCatDates.value
}