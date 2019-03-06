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
        if (pathFromCache != null) return StringValue(pathFromCache)
        val eType = EntityTypeManager.getEntityType(eTypeName)
            ?: throw PluginException("There is no group relations information, entity<$eTypeName>")
        val path = mutableListOf<String>()
        val groupCode = entity.data[groupCodeAttr]
            ?: throw PluginException("OneC group<id:${entity.id}> has not group code attribute")
        var relationEntity = DbEntity
            .getByAttribute(eType, codeAttr, groupCode)
            .firstOrNull()
            ?: return null
        var name = relationEntity.readAttribute(nameAttr)?.value as? String
        while (name != null) {
            path.add(name)
            val parent = relationEntity.readAttribute(parentAttr) as? LongValue ?: break
            relationEntity = DbEntity.getByAttribute(eType, codeAttr, parent).firstOrNull() ?: break
            name = relationEntity.readAttribute(nameAttr)?.value as? String
        }
        return StringValue("$root${path.reversed().joinToString(separator)}").also {
            pathCache[entity.id.value] = WeakReference(it.value)
        }
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<Int, WeakReference<String>>(200, 100))
        private const val eTypeName = "onec-group-relation"
        private val relationEntityType = EntityTypeManager[eTypeName]
        private val groupEntityType = EntityTypeManager["onec-group"]
        private val codeAttr by lazy {
            EntityTypeManager.getEntityAttribute(relationEntityType, "group_code")!!
        }
        private val groupCodeAttr by lazy {
            EntityTypeManager.getEntityAttribute(groupEntityType, "group_code")!!
        }
        private val nameAttr by lazy {
            EntityTypeManager.getEntityAttribute(relationEntityType, "group_name")!!
        }
        private val parentAttr by lazy {
            EntityTypeManager.getEntityAttribute(relationEntityType,"group_parent_code")!!
        }
    }
}
