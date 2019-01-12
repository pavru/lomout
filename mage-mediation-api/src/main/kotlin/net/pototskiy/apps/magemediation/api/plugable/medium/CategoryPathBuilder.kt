package net.pototskiy.apps.magemediation.api.plugable.medium

import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.api.plugable.Plugable

interface CategoryPathBuilder: Plugable {
    fun setCategoryEntities(entities: CategoryEntityClass)
    fun buildPath(entity: CategoryEntity): String
}