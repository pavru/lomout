package net.pototskiy.apps.magemediation.medium

import net.pototskiy.apps.magemediation.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.database.onec.GroupEntity

interface CategoryPathBuilder {
    fun buildNamePath(entity: GroupEntity, separator: String = "/"): String
    fun buildCodePath(entity: GroupEntity, separator: String = "/"): String
    fun buildNamePath(entity: CategoryEntity, separator: String = "/"): String
    fun buildCodePath(entity: CategoryEntity, separator: String = "/"): String
}