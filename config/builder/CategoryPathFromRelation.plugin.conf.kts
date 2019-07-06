import MageCategory_conf.MageCategory
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.plugable.PluginContext.repository
import org.apache.commons.collections4.map.LRUMap
import org.bson.types.ObjectId
import java.lang.ref.WeakReference
import java.util.Collections.*

class CategoryPathFromRelation (
    private val separator: String = "/",
    private val root: String = ""
): AttributeBuilder<String?>() {

    override fun build(entity: Document): String? {
        entity as MageCategory
        val cachedPath = pathCache[entity._id]?.get()
        if (cachedPath != null) return cachedPath
        val path = mutableListOf<String>()
        var current: MageCategory? = entity
        var name: String? = entity.name
        while (name != null && current != null) {
            path.add(name)
            val parentId = current.parent_id
            current = current.let {
                repository.get(
                    MageCategory::class,
                    mapOf(MageCategory.attributes.getValue("parent_id") to parentId)
                ) as? MageCategory
            }
            name = current?.let { it.name }
        }
        return "$root${path.reversed().joinToString(separator)}".also {
            pathCache[entity._id] = WeakReference(it)
        }
    }

    companion object {
        private val pathCache = synchronizedMap(LRUMap<ObjectId, WeakReference<String>>(200, 100))
        private const val eTypeName = "mage-category"
    }
}
