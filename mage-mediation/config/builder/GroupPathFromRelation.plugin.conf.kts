@file:DependsOn("org.jetbrains.exposed:exposed:0.12.1")

import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.plugable.*
import org.apache.commons.collections4.map.LRUMap
import org.jetbrains.kotlin.script.util.DependsOn
import java.lang.ref.WeakReference
import java.util.Collections.synchronizedMap

public class GroupPathFromRelation : AttributeBuilderPlugin<StringType>() {
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
        return StringValue("${root}${path.reversed().joinToString(separator)}").also {
            pathCache[entity.id.value] = WeakReference(it.value)
        }
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<Int, WeakReference<String>>(200, 100))
        private const val eTypeName = "onec-group-relation"
        private val codeAttr by lazy {
            EntityAttributeManager.getAttributeOrNull(AttributeName(eTypeName, "group_code"))!!
        }
        private val groupCodeAttr by lazy {
            EntityAttributeManager.getAttributeOrNull(AttributeName("onec-group", "group_code"))!!
        }
        private val nameAttr by lazy {
            EntityAttributeManager.getAttributeOrNull(AttributeName(eTypeName, "group_name"))!!
        }
        private val parentAttr by lazy {
            EntityAttributeManager.getAttributeOrNull(AttributeName(eTypeName,"group_parent_code"))!!
        }
    }
}
