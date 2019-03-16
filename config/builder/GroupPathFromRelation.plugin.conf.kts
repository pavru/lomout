//@file:Repository("","jcenter","https://jcenter.bintray.com/")
//@file:DependsOn("org.jetbrains.exposed:exposed:0.12.2")

import org.apache.commons.collections4.map.LRUMap
import java.lang.ref.WeakReference
import java.util.Collections.*
import kotlin.collections.set

class GroupPathFromRelation : AttributeBuilderPlugin<StringType>() {
    var separator: String = "/"
    var root: String = ""

    override fun build(entity: DbEntity): StringType? {
        val pathFromCache = pathCache[entity.id.value]?.get()
        if (pathFromCache != null) return StringType(pathFromCache)
        val eType = entityTypeManager.getEntityType(eTypeName)
            ?: throw AppPluginException("There is no group relations information, entity<$eTypeName>")
        val path = mutableListOf<String>()
        val groupCode = entity.data[groupCodeAttr]
            ?: throw AppPluginException("OneC group<id:${entity.id}> has not group code attribute")
        var relationEntity = DbEntity
            .getByAttribute(eType, codeAttr, groupCode)
            .firstOrNull()
            ?: return null
        var name = relationEntity.readAttribute(nameAttr)?.value as? String
        while (name != null) {
            path.add(name)
            val parent = relationEntity.readAttribute(parentAttr) as? LongType ?: break
            relationEntity = DbEntity.getByAttribute(eType, codeAttr, parent).firstOrNull() ?: break
            name = relationEntity.readAttribute(nameAttr)?.value as? String
        }
        return StringType("$root${path.reversed().joinToString(separator)}").also {
            pathCache[entity.id.value] = WeakReference(it.value)
        }
    }

    companion object {
        private val typeManager by lazy { PluginContext.entityTypeManager }
        private val pathCache = synchronizedMap(LRUMap<Int, WeakReference<String>>(200, 100))
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
