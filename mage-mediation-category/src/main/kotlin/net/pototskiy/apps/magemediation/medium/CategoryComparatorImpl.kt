package net.pototskiy.apps.magemediation.medium

import net.pototskiy.apps.magemediation.config.Config
import net.pototskiy.apps.magemediation.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.database.mage.CategoryTable
import net.pototskiy.apps.magemediation.database.onec.GroupEntity
import net.pototskiy.apps.magemediation.database.onec.GroupEntityClass
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryComparatorImpl(
    private val mageCategories: CategoryEntityClass,
    private val onecGroups: GroupEntityClass,
    private val config: Config
) : CategoryComparator {

    override fun findMageCategoryByOnecCode(entityID: Long): CategoryEntity? {
        return transaction {
            mageCategories.find {
                (mageCategories.table as CategoryTable).entityID eq entityID
            }.firstOrNull()
        }
    }

    override fun findMageCategoryByOnecPath(path: String): CategoryEntity? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findOnecGroupByMageEntityID(code: String): GroupEntity? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findOnecGroupByMagePath(path: String): GroupEntity? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mapOnecGroupToMageEntityID(groupCode: String): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mapOnecPathToMagePath(path: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mapMageEntityIDToOnecGroup(entityID: Long): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mapMagePathToOnecPath(path: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}