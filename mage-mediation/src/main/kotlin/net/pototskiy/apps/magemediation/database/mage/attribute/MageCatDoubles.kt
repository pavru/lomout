package net.pototskiy.apps.magemediation.database.mage.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mage.MageCategories
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object MageCatDoubles : TypedAttributeTable<Double>("mage_cat_double") {
    override val owner = reference("category", MageCategories, ReferenceOption.CASCADE)
    override val value = double("value")
}

class MageCatDouble(id: EntityID<Int>): TypedAttributeEntity<Double>(id) {
    companion object: TypedAttributeEntityClass<Double, MageCatDouble>(MageCatDoubles)

    override var owner: IntEntity by MageCategory referencedOn MageCatDoubles.owner
    override var index by MageCatDoubles.index
    override var code by MageCatDoubles.code
    override var value by MageCatDoubles.value
}