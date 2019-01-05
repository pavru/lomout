package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.CONFIG
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import net.pototskiy.apps.magemediation.database.onec.OnecGroup
import net.pototskiy.apps.magemediation.medium.CategoryPlugin
import net.pototskiy.apps.magemediation.medium.CategoryPluginImpl
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryMediator : AbstractMediator {
    private val plugin: CategoryPlugin = CategoryPluginImpl()

    init {
        plugin.configure(
            MageCategory.Companion,
            OnecGroup.Companion,
            CONFIG.config
        )
    }

    override fun merge() {
        val comparator = plugin.comparator
        val recordTools = plugin.recordTools
        val pathBuilder = plugin.pathBuilder

        transaction { MediumCategories.deleteAll() }
        transaction {
            MageCategory.all().forEach {
                val groupExists = comparator.isThereOnecGroupByEntityID(
                    comparator.mapMageEntityIDToOnecGroup(it.entityID)
                ) || comparator.isThereOnecGroupByPath(pathBuilder.buildNamePath(it))
                if (!groupExists) {
                    recordTools.createOnecGroupFromMageCategory(it.entityID)
                }
            }
            OnecGroup.all().forEach {
                val categoryExists = comparator.isThereMageCategoryByGroupCode(
                    comparator.mapOnecGroupToMageEntityID(it.groupCode)
                ) || comparator.isThereMageCategoryByPath(pathBuilder.buildNamePath(it))
                if (!categoryExists) {
                    recordTools.createMageCategoryFromOnecGroup(it.groupCode)
                }
            }
        }
    }
}