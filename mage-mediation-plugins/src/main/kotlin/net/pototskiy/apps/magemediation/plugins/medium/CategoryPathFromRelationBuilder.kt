package net.pototskiy.apps.magemediation.plugins.medium

import net.pototskiy.apps.magemediation.api.config.type.AttributeDescription
import net.pototskiy.apps.magemediation.api.config.type.AttributeIntType
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.api.plugable.Plugin
import net.pototskiy.apps.magemediation.api.plugable.medium.CategoryPathBuildInterface
import org.jetbrains.exposed.dao.EntityID
import java.lang.ref.WeakReference

class CategoryPathFromRelationBuilder : CategoryPathBuildInterface {
    private lateinit var entities: CategoryEntityClass
    private val config = Plugin.config.mediator.magento.category.pathAttribute

    override fun build(entity: CategoryEntity, args: Map<String, Any>): String {
        val separator = args[ARG_SEPARATOR] as? String ?: "/"
        val root = args[ARG_ROOT] as? String ?: "/"

        val cachedPath = cachedPaths[entity.id]?.get()
        if (cachedPath != null) return cachedPath
        val path = mutableListOf<String>()
        var current: CategoryEntity? = entity
        var name = getName(entity)
        while (name != null) {
            path.add(name)
            current = current?.let { getParent(it) }
            name = current?.let { getName(it) }
        }
        return "$root${path.reversed().joinToString(separator)}".also {
            cachedPaths[entity.id] = WeakReference(it)
        }
    }

    private fun getParent(current: CategoryEntity): CategoryEntity? {
        return entities.getAttribute(current, parentAttr)?.let {
            entities.findByAttribute(entityIDAttr, it).firstOrNull()
        }
    }

    private fun getName(entity: CategoryEntity): String? {
        return entities.getAttribute(entity, nameAttr) as? String
    }

    companion object {
        private const val ARG_SEPARATOR = "separator"
        private const val ARG_ROOT = "root"

        private val cachedPaths = mutableMapOf<EntityID<Int>, WeakReference<String>>()
        private val entityIDAttr = AttributeDescription(
            "entity_id",
            AttributeIntType(false),
            false,
            false,
            false,
            null
        )
        private val nameAttr = AttributeDescription(
            "name",
            AttributeStringType(false),
            false,
            false,
            false,
            null
        )
        private val parentAttr = AttributeDescription(
            "parent_id",
            AttributeIntType(false),
            false,
            false,
            false,
            null
        )
    }
}
