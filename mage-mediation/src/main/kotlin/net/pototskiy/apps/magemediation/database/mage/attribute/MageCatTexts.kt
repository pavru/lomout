package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MageCatTexts: TypedAttributeTable<String>("mage_cat_text") {
    override val owner = reference("category", MageCategories, ReferenceOption.CASCADE)
    override val value = text("value")
}

class MageCatText(id: EntityID<Int>): TypedAttributeEntity<String>(id) {
    companion object: TypedAttributeEntityClass<String, MageCatText>(MageCatTexts)

    override var owner: IntEntity by MageCategory referencedOn MageCatTexts.owner
    override var index by MageCatTexts.index
    override var code by MageCatTexts.code
    override var value by MageCatTexts.value
}