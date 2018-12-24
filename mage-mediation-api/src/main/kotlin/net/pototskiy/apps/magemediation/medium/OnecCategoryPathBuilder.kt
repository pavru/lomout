package net.pototskiy.apps.magemediation.medium

import net.pototskiy.apps.magemediation.database.onec.GroupEntityClass

interface OnecCategoryPathBuilder {
    fun setOnecCategoryEntityClassObject(entityClass: GroupEntityClass)
    fun buildNamePath(code: String, separator: String = "/")
    fun buildCodePath(code: String, separator: String = "/")
}