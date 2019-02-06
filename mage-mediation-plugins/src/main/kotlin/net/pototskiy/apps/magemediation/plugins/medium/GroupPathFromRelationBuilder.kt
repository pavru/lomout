package net.pototskiy.apps.magemediation.plugins.medium

import net.pototskiy.apps.magemediation.api.config.type.AttributeDescription
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.onec.GroupEntity
import net.pototskiy.apps.magemediation.api.database.onec.GroupEntityClass
import net.pototskiy.apps.magemediation.api.database.onec.GroupRelationEntityClass
import net.pototskiy.apps.magemediation.api.plugable.medium.GroupPathBuildInterface
import org.jetbrains.exposed.dao.EntityID
import java.lang.ref.WeakReference

class GroupPathFromRelationBuilder : GroupPathBuildInterface {
    private lateinit var groups: GroupEntityClass
    private lateinit var relations: GroupRelationEntityClass

    override fun build(entity: GroupEntity, args: Map<String, Any>): String {
        val separator = args[ARG_SEPARATOR] as? String ?: "/"
        val root = args[ARG_ROOT] as? String ?: "/"

        val pathFromCache = cachedPath[entity.id]?.get()
        if (pathFromCache != null) return pathFromCache
        val path = mutableListOf<String>()
        val groupCode = groups.getAttribute(entity, codeAttr)
            ?: throw DatabaseException("OneC group<id:${entity.id}> has not group code attribute")
        var relationEntity = relations
            .findByAttribute(codeAttr, groupCode)
            .firstOrNull()
            ?: throw DatabaseException("OneC group relation data has no information for group<$groupCode>")
        var name = relations.getAttribute(relationEntity, nameAttr) as? String
        while (name != null) {
            path.add(name)
            val parent = relations.getAttribute(relationEntity, parentAttr) as? String
                ?: break
            if (parent.isBlank()) break
            relationEntity = relations.findByAttribute(codeAttr, parent)
                .firstOrNull()
                    ?: break
            name = relations.getAttribute(relationEntity, nameAttr) as? String
        }
        return "$root${path.reversed().joinToString(separator)}".also {
            cachedPath[entity.id] = WeakReference(it)
        }
    }

    companion object {
        private const val ARG_SEPARATOR = "separator"
        private const val ARG_ROOT = "root"

        private val cachedPath = mutableMapOf<EntityID<Int>, WeakReference<String>>()
        private val codeAttr = AttributeDescription(
            "group_code",
            AttributeStringType(false),
            false,
            false,
            false,
            null
        )
        private val nameAttr = AttributeDescription(
            "group_name",
            AttributeStringType(false),
            false,
            false,
            false,
            null
        )
        private val parentAttr = AttributeDescription(
            "group_parent_code",
            AttributeStringType(false),
            false,
            false,
            false,
            null
        )
    }
}
