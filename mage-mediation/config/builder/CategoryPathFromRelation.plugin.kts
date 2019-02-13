@file:DependsOn("org.jetbrains.exposed:exposed:0.12.1")

package builder

import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.plugable.AttributeBuilderPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.kotlin.script.util.DependsOn
import java.lang.ref.WeakReference
import java.util.Collections.synchronizedMap

class CategoryPathFromRelation : AttributeBuilderPlugin<PersistentSourceEntity, String?>() {
    private var separator: String = "/"
    private var root: String = "/"
    override fun setOptions(options: NewPlugin.Options) {
        super.setOptions(options)
        (options as? Options)?.let {
            this.separator = it.separator ?: "/"
            this.root = it.root ?: "/"
        }
    }

    override fun optionSetter(): NewPlugin.Options {
        return Options()
    }

    override fun execute(): String? {
        EntityClass.getClass("mage-category")?.let {
            categories = it
        } ?: return null
        val cachedPath = pathCache[entity.id.value]?.get()
        if (cachedPath != null) return cachedPath
        val path = mutableListOf<String>()
        var current: PersistentSourceEntity? = entity
        var name = entity.readAttribute(nameAttr) as? String
        while (name != null && current != null) {
            path.add(name)
            val parentId = current.readAttribute(parentAttr) as Long
            current = current.let {
                categories.getByAttribute(idAttr, parentId).firstOrNull() as PersistentSourceEntity?
            }
            name = current?.let { it.readAttribute(nameAttr) as? String }
        }
        return@execute "$root${path.reversed().joinToString(separator)}".also {
            pathCache[entity.id.value] = WeakReference(it)
        }
    }

    class Options : AttributeBuilderPlugin.Options() {
        var separator: String? = null
        var root: String? = null
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<Int, WeakReference<String>>(200, 100))
        private lateinit var categories: EntityClass<*>
        private val nameAttr by lazy { categories.attributes.find { it.name == "name" }!! }
        private val idAttr by lazy { categories.attributes.find { it.name == "entity_id" }!! }
        private val parentAttr by lazy { categories.attributes.find { it.name == "parent_id" }!! }
    }
}

//val categoryPathFromRelationPlugin = attributeBuilderPlugin<CategoryEntity, String> {
//    parameter("separator", "/")
//    parameter("root", "/")
//    execute {
//        val separator = getArgument(separatorParameter) ?: "/"
//        val root = getArgument(rootParameter) ?: ""
//
//        val cachedPath = cachedPaths[entity.id]?.get()
//        if (cachedPath != null) return@execute cachedPath
//        val path = mutableListOf<String>()
//        var current: CategoryEntity? = entity
//        var name = getName(entity)
//        while (name != null) {
//            path.add(name)
//            current = current?.let { getParent(it) }
//            name = current?.let { getName(it) }
//        }
//        return@execute "$root${path.reversed().joinToString(separator)}".also {
//            cachedPaths[entity.id] = WeakReference(it)
//        }
//    }
//}
//
//fun getParent(current: CategoryEntity): CategoryEntity? {
//    val entities = current::class.companionObjectInstance as? CategoryEntityClass
//        ?: throw PluginException("Category entity is not well defined")
//    return entities.getAttribute(current, parentAttr)?.let {
//        entities.findByAttribute(entityIDAttr, it).firstOrNull()
//    }
//}
//
//fun getName(entity: CategoryEntity): String? {
//    val entities = entity::class.companionObjectInstance as? CategoryEntityClass
//        ?: throw PluginException("Category entity is not well defined")
//    return entities.getAttribute(entity, nameAttr) as? String
//}

//class CategoryPathFromRelationBuilder : CategoryPathBuildInterface {
//    private lateinit var entities: CategoryEntityClass
//    private val config = Plugin.config.mediator.magento.category.pathAttribute
//
//    override fun build(entity: CategoryEntity, args: Map<String, Any>): String {
//        val separator = args[separatorParameter] as? String ?: "/"
//        val root = args[rootParameter] as? String ?: "/"
//
//        val cachedPath = cachedPaths[entity.id]?.get()
//        if (cachedPath != null) return cachedPath
//        val path = mutableListOf<String>()
//        var current: CategoryEntity? = entity
//        var name = getName(entity)
//        while (name != null) {
//            path.add(name)
//            current = current?.let { getParent(it) }
//            name = current?.let { getName(it) }
//        }
//        return "$root${path.reversed().joinToString(separator)}".also {
//            cachedPaths[entity.id] = WeakReference(it)
//        }
//    }
//
//    private fun getParent(current: CategoryEntity): CategoryEntity? {
//        return entities.getAttribute(current, parentAttr)?.let {
//            entities.findByAttribute(entityIDAttr, it).firstOrNull()
//        }
//    }
//
//    private fun getName(entity: CategoryEntity): String? {
//        return entities.getAttribute(entity, nameAttr) as? String
//    }
//
//    companion object {
//        private const val separatorParameter = "separator"
//        private const val rootParameter = "root"
//
//        private val cachedPaths = mutableMapOf<EntityID<Int>, WeakReference<String>>()
//        private val entityIDAttr = AttributeDescription(
//            "entity_id",
//            AttributeIntType(false),
//            false,
//            false,
//            false,
//            null
//        )
//        private val nameAttr = AttributeDescription(
//            "name",
//            AttributeStringType(false),
//            false,
//            false,
//            false,
//            null
//        )
//        private val parentAttr = AttributeDescription(
//            "parent_id",
//            AttributeIntType(false),
//            false,
//            false,
//            false,
//            null
//        )
//    }
//}
