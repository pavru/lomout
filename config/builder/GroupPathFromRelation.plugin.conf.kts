import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.exposed.dao.EntityID
import java.lang.ref.WeakReference
import java.util.Collections.*
import kotlin.collections.set

class GroupPathFromRelation : AttributeBuilderPlugin<STRING>() {
    var separator: String = "/"
    var root: String = ""

    override fun build(entity: Entity): STRING? {
        logger.info("test logger")
        val pathFromCache = pathCache[entity.id]?.get()
        if (pathFromCache != null) return STRING(pathFromCache)
        val eType = entityTypeManager.getEntityType(eTypeName)
            ?: throw AppConfigException(
                badPlace(entity.type),
                "There is no group relations information."
            )
        val path = mutableListOf<String>()
        val groupCode = entity.data[groupCodeAttr]
            ?: throw AppConfigException(
                badPlace(entity.type),
                "OneC group id '${entity.id}' has not group code attribute."
            )
        var relationEntity = repository.get(
            eType,
            mapOf(codeAttr to groupCode),
            EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
        ) ?: return null
        var name = relationEntity[nameAttr]?.value as? String
        while (name != null) {
            path.add(name)
            val parent = relationEntity[parentAttr] as? LONG ?: break
            relationEntity = repository.get(
                eType,
                mapOf(codeAttr to parent),
                EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
            ) ?: break
            name = relationEntity[nameAttr]?.value as? String
        }
        return STRING("$root${path.reversed().joinToString(separator)}").also {
            pathCache[entity.id] = WeakReference(it.value)
        }
    }

    companion object {
        private val typeManager by lazy { PluginContext.entityTypeManager }
        private val pathCache = synchronizedMap(LRUMap<EntityID<Int>, WeakReference<String>>(200, 100))
        private const val eTypeName = "onec-group-relation"
        private val relationEntityType by lazy { typeManager[eTypeName] }
        private val groupEntityType by lazy { typeManager["onec-group"] }
        private val codeAttr by lazy {
            typeManager.getEntityAttribute(relationEntityType, "group_code")!!
        }
        private val groupCodeAttr by lazy {
            typeManager.getEntityAttribute(groupEntityType, "group_code")!!
        }
        private val nameAttr by lazy {
            typeManager.getEntityAttribute(relationEntityType, "group_name")!!
        }
        private val parentAttr by lazy {
            typeManager.getEntityAttribute(relationEntityType, "group_parent_code")!!
        }
    }
}
