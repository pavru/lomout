package net.pototskiy.apps.magemediation.medium

import net.pototskiy.apps.magemediation.config.Config
import net.pototskiy.apps.magemediation.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.database.onec.GroupEntityClass

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