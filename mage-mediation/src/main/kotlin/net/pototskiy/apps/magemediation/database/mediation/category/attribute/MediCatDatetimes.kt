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

object MediCatDatetimes : TypedAttributeTable<DateTime>("medi_cat_datetime") {
    override val owner = reference("category", MediCategories, ReferenceOption.CASCADE)
    override val value = datetime("value")
}

class MediCatDatetime(id: EntityID<Int>): TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MediCatDatetime>(MediCatDatetimes)

    override var owner: IntEntity by MediCategory referencedOn MediCatDatetimes.owner
    override var index by MediCatDatetimes.index
    override var code by MediCatDatetimes.code
    override var value by MediCatDatetimes.value
}