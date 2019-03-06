//@file:Repository("","jcenter","https://jcenter.bintray.com/")
//@file:DependsOn("org.jetbrains.exposed:exposed:0.12.2")

import org.apache.commons.collections4.map.LRUMap
import java.lang.ref.WeakReference
import java.util.Collections.*
import kotlin.collections.set

class CategoryPathFromRelation : AttributeBuilderPlugin<StringType>() {
    var separator: String = "/"
    var root: String = ""

    override fun build(entity: DbEntity): StringType? {
        val cachedPath = pathCache[entity.id.value]?.get()
        if (cachedPath != null) return StringValue(cachedPath)
        val eType = entity.eType
        val path = mutableListOf<String>()
        var current: DbEntity? = entity
        var name = entity.readAttribute(nameAttr)?.value as String?
        while (name != null && current != null) {
            path.add(name)
            val parentId = current.readAttribute(parentAttr) as LongValue
            current = current.let {
                DbEntity.getByAttribute(eType, idAttr, parentId).firstOrNull()
            }
            name = current?.let { it.readAttribute(nameAttr)?.value as? String }
        }
        return StringValue("$root${path.reversed().joinToString(separator)}").also {
            pathCache[entity.id.value] = WeakReference(it.value)
        }
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<Int, WeakReference<String>>(200, 100))
        private const val eTypeName = "mage-category"
        private val entityType = EntityTypeManager[eTypeName]
        private val nameAttr by lazy { EntityTypeManager.getEntityAttribute(entityType, "name")!! }
        private val idAttr by lazy { EntityTypeManager.getEntityAttribute(entityType, "entity_id")!! }
        private val parentAttr by lazy { EntityTypeManager.getEntityAttribute(entityType, "parent_id")!! }
    }
}
