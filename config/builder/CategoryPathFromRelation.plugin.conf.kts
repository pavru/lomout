import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.exposed.dao.EntityID
import java.lang.ref.WeakReference
import java.util.Collections.*
import kotlin.collections.set

class CategoryPathFromRelation : AttributeBuilderPlugin<STRING>() {
    var separator: String = "/"
    var root: String = ""

    override fun build(entity: Entity): STRING? {
        val cachedPath = pathCache[entity.id]?.get()
        if (cachedPath != null) return STRING(cachedPath)
        val eType = entity.type
        val path = mutableListOf<String>()
        var current: Entity? = entity
        var name = entity[nameAttr]?.value as String?
        while (name != null && current != null) {
            path.add(name)
            val parentId = current[parentAttr] as LONG
            current = current.let {
                repository.get(
                    eType,
                    mapOf(idAttr to parentId),
                    EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
                )
            }
            name = current?.let { it[nameAttr]?.value as? String }
        }
        return STRING("$root${path.reversed().joinToString(separator)}").also {
            pathCache[entity.id] = WeakReference(it.value)
        }
    }

    companion object {
        private val typeManager by lazy { PluginContext.entityTypeManager }
        private val pathCache = synchronizedMap(LRUMap<EntityID<Int>, WeakReference<String>>(200, 100))
        private const val eTypeName = "mage-category"
        private val entityType by lazy { typeManager[eTypeName] }
        private val nameAttr by lazy { typeManager.getEntityAttribute(entityType, "name")!! }
        private val idAttr by lazy { typeManager.getEntityAttribute(entityType, "entity_id")!! }
        private val parentAttr by lazy { typeManager.getEntityAttribute(entityType, "parent_id")!! }
    }
}
