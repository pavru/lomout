package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MageCatVarchars : TypedAttributeTable<String>("mage_cat_varchar") {
    override val owner = reference("category", MageCategories, ReferenceOption.CASCADE)
    override val value = varchar("value", 800)
}

class MageCatVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MageCatVarchar>(MageCatVarchars)

    override var owner: IntEntity by MageCategory referencedOn MageCatVarchars.owner
    override var index by MageCatVarchars.index
    override var code by MageCatVarchars.code
    override var value by MageCatVarchars.value
}