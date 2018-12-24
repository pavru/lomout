package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object MageCatDatetimes : TypedAttributeTable<DateTime>("mage_cat_datetime") {
    override val owner = reference("category", MageCategories, ReferenceOption.CASCADE)
    override val value = datetime("value")
}

class MageCatDatetime(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, MageCatDatetime>(MageCatDatetimes)

    override var owner: IntEntity by MageCategory referencedOn MageCatDatetimes.owner
    override var index by MageCatDatetimes.index
    override var code by MageCatDatetimes.code
    override var value by MageCatDatetimes.value
}