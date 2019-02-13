@file:DependsOn("org.jetbrains.exposed:exposed:0.12.1")

package builder

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.kotlin.script.util.DependsOn
import java.lang.ref.WeakReference
import java.util.Collections.synchronizedMap

public class GroupPathFromRelation : AttributeBuilderPlugin<PersistentSourceEntity, String?>() {
    private var separator: String = "/"
    private var root: String = "/"

    override fun execute(): String? {
        EntityClass.getClass("onec-group-relation")?.let {
            relations = it
        } ?: return null
        val pathFromCache = pathCache[entity.id.value]?.get()
        if (pathFromCache != null) return pathFromCache
        val path = mutableListOf<String>()
        val groupCode = entity.data[codeAttr]
            ?: throw DatabaseException("OneC group<id:${entity.id}> has not group code attribute")
        var relationEntity = relations
            .getByAttribute(codeAttr, groupCode)
            .firstOrNull()
            ?: throw DatabaseException("OneC group relation data has no information for group<$groupCode>")
        var name = relationEntity.readAttribute(nameAttr) as? String
        while (name != null) {
            path.add(name)
            val parent = relationEntity.readAttribute(parentAttr) as? Long ?: break
            relationEntity = relations.getByAttribute(codeAttr, parent).firstOrNull() ?: break
            name = relationEntity.readAttribute(nameAttr) as? String
        }
        return "$root${path.reversed().joinToString(separator)}".also {
            pathCache[entity.id.value] = WeakReference(it)
        }
    }

    override fun setOptions(options: NewPlugin.Options) {
        super.setOptions(options)
        (options as? Options)?.let {
            separator = it.separator ?: "/"
            root = it.root ?: "/"
        }
    }

    override fun optionSetter(): NewPlugin.Options {
        return Options()
    }

    class Options : AttributeBuilderPlugin.Options() {
        var separator: String? = null
        var root: String? = null
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<Int, WeakReference<String>>(200, 100))
        private lateinit var relations: EntityClass<*>
        private val codeAttr by lazy { relations.attributes.find { it.name == "group_code" }!! }
        private val nameAttr by lazy { relations.attributes.find { it.name == "group_name" }!! }
        private val parentAttr by lazy { relations.attributes.find { it.name == "group_parent_code" }!! }
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
