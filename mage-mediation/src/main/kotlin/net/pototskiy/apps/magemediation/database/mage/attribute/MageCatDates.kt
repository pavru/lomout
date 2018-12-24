package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object MageCatDates : TypedAttributeTable<DateTime>("mage_cat_date") {
    override val owner = reference("category", MageCategories, ReferenceOption.CASCADE)
    override val value = date("value")
}

class MageCatDate(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MageCatDate>(MageCatDates)

    override var owner: IntEntity by MageCategory referencedOn MageCatDates.owner
    override var index by MageCatDates.index
    override var code by MageCatDates.code
    override var value by MageCatDates.value
}