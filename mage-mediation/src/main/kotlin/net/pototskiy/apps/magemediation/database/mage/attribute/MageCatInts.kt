package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MageCatInts : TypedAttributeTable<Long>("mage_cat_int") {
    override val owner = reference("category", MageCategories, onDelete = ReferenceOption.CASCADE)
    override val value = long("value")
}

class MageCatInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, MageCatInt>(MageCatInts)

    override var owner: IntEntity by MageCategory referencedOn MageCatInts.owner
    override var index by MageCatInts.index
    override var code by MageCatInts.code
    override var value by MageCatInts.value
}