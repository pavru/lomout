package net.pototskiy.apps.magemediation.medium

import net.pototskiy.apps.magemediation.config.Config
import net.pototskiy.apps.magemediation.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.database.onec.GroupEntityClass

class CategoryPluginImpl : CategoryPlugin {
    private lateinit var mageCategories: CategoryEntityClass
    private lateinit var onecGroups: GroupEntityClass
    private lateinit var config: Config

    override val comparator: CategoryComparator
        get() = CategoryComparatorImpl(mageCategories, onecGroups, config)
    override val pathBuilder: CategoryPathBuilder
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val recordTools: CategoryRecordTools
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun configure(
        mageCategories: CategoryEntityClass,
        onecGroups: GroupEntityClass,
        config: Config
    ) {
        this.mageCategories = mageCategories
        this.onecGroups = onecGroups
        this.config = config
    }
}