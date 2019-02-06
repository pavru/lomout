@file:DependsOn("org.jetbrains.exposed:exposed:0.12.1")

import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.config.type.AttributeLongType
import net.pototskiy.apps.magemediation.api.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.api.database.newschema.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.plugable.Parameter
import net.pototskiy.apps.magemediation.api.plugable.attributeBuilderPlugin
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.kotlin.script.util.DependsOn
import java.lang.ref.WeakReference

val separatorParameter = Parameter<String>("separator")
val rootParameter = Parameter<String>("root")

val entityIDAttr = Attribute("entity_id", AttributeLongType(false), false, false, false, null)
val nameAttr = Attribute("name", AttributeStringType(false), false, false, false, null)
val parentAttr = Attribute("parent_id", AttributeLongType(false), false, false, false, null)

val cachedPaths = mutableMapOf<EntityID<Int>, WeakReference<String>>()

val categoryPathFromRelationPlugin = attributeBuilderPlugin<PersistentSourceEntity, String> {execute { "" }}
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
