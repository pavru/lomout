package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import org.jetbrains.exposed.dao.EntityID

object MageCatTexts : TypedAttributeTable<String>(
    "mage_cat_text",
    MageCategories,
    { text("value") }
)

class MageCatText(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, MageCatText>(MageCatTexts, String::class)
}