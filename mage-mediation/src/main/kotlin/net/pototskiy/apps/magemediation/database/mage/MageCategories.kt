package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.api.database.mage.CategoryTable
import net.pototskiy.apps.magemediation.database.mage.attribute.*
import org.jetbrains.exposed.dao.EntityID

object MageCategories : CategoryTable("mage_category")

class MageCategory(id: EntityID<Int>) : CategoryEntity(id) {
    companion object : CategoryEntityClass(
        MageCategories,
        null,
        MageCatBool.Companion,
        MageCatDate.Companion,
        MageCatDatetime.Companion,
        MageCatDouble.Companion,
        MageCatInt.Companion,
        MageCatText.Companion,
        MageCatVarchar.Companion
    )

    override var entityID by MageCategories.entityID
}
