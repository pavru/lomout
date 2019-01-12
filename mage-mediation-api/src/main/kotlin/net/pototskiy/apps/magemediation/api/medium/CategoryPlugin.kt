package net.pototskiy.apps.magemediation.api.medium

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.api.database.onec.GroupEntityClass
import net.pototskiy.apps.magemediation.api.plugable.medium.CategoryPathBuilder

interface CategoryPlugin {
    val comparator: CategoryComparator
    val pathBuilder: CategoryPathBuilder
    val recordTools: CategoryRecordTools

    fun configure(
        mageCategories: CategoryEntityClass,
        onecGroups: GroupEntityClass,
        config: Config
    )
}