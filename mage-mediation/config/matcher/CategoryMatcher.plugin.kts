package matcher

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.AttributeStringType
import net.pototskiy.apps.magemediation.api.database.EntityClass
import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherPlugin

public class CategoryMatcher : EntityMatcherPlugin() {
    override fun execute(): Boolean {
        val group = entities["onec-group"] ?: return false
        val category = entities["mage-category"] ?: return false
        if (group.mappedData[entityIDAttr] == category.origData[entityIDAttr]) return true
        if (group.mappedData[pathAttr] == category.origData[pathAttr]) return true
        return false
    }

    companion object {
        private val groupEntityClass by lazy { EntityClass.getClass("onec-group")!! }
        private val categoryEntityClass by lazy { EntityClass.getClass("mage-category")!! }
        private val entityIDAttr by lazy { categoryEntityClass.attributes.find { it.name == "entity_id" }!! }
        private val pathAttr by lazy { categoryEntityClass.attributes.find { it.name == "__path" }!! }
    }
}
