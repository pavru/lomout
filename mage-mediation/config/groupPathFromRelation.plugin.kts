@file:DependsOn("org.jetbrains.exposed:exposed:0.12.1")

import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.plugable.attributeBuilderPlugin
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.kotlin.script.util.DependsOn
import java.lang.ref.WeakReference

private val argSeparator = "separator"
private val argRoot = "root"
private val cachedPath = mutableMapOf<EntityID<Int>, WeakReference<String>>()

val groupPathFromRelationPlugin = attributeBuilderPlugin<PersistentSourceEntity, String?> {
    parameter("separator", "/")
    parameter("root", "/")
    execute {
        val v = "test".drop(1)
        null
    }
}

//class GroupPathFromRelationBuilder : GroupPathBuildInterface {
//    private lateinit var groups: GroupEntityClass
//    private lateinit var relations: GroupRelationEntityClass
//
//    override fun build(entity: GroupEntity, args: Map<String, Any>): String {
//        val separator = args[argSeparator] as? String ?: "/"
//        val root = args[argRoot] as? String ?: "/"
//
//        val pathFromCache = cachedPath[entity.id]?.get()
//        if (pathFromCache != null) return pathFromCache
//        val path = mutableListOf<String>()
//        val groupCode = groups.getAttribute(entity, codeAttr)
//            ?: throw DatabaseException("OneC group<id:${entity.id}> has not group code attribute")
//        var relationEntity = relations
//            .findByAttribute(codeAttr, groupCode)
//            .firstOrNull()
//            ?: throw DatabaseException("OneC group relation data has no information for group<$groupCode>")
//        var name = relations.getAttribute(relationEntity, nameAttr) as? String
//        while (name != null) {
//            path.add(name)
//            val parent = relations.getAttribute(relationEntity, parentAttr) as? String
//                ?: break
//            if (parent.isBlank()) break
//            relationEntity = relations.findByAttribute(codeAttr, parent)
//                .firstOrNull()
//                ?: break
//            name = relations.getAttribute(relationEntity, nameAttr) as? String
//        }
//        return "$root${path.reversed().joinToString(separator)}".also {
//            cachedPath[entity.id] = WeakReference(it)
//        }
//    }
//
//    companion object {
//        private const val argSeparator = "separator"
//        private const val argRoot = "root"
//
//        private val cachedPath = mutableMapOf<EntityID<Int>, WeakReference<String>>()
//        private val codeAttr = AttributeDescription(
//            "group_code",
//            AttributeStringType(false),
//            false,
//            false,
//            false,
//            null
//        )
//        private val nameAttr = AttributeDescription(
//            "group_name",
//            AttributeStringType(false),
//            false,
//            false,
//            false,
//            null
//        )
//        private val parentAttr = AttributeDescription(
//            "group_parent_code",
//            AttributeStringType(false),
//            false,
//            false,
//            false,
//            null
//        )
//    }
//}
