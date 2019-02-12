package matcher

import net.pototskiy.apps.magemediation.api.config.data.Attribute
import net.pototskiy.apps.magemediation.api.config.data.AttributeStringType
import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherPlugin

public class CategoryMatcher : EntityMatcherPlugin() {
    override fun execute(): Boolean {
        val group = entities["onec-group"] ?: return false
        val category = entities["mage-category"] ?: return false
        if (group.mappedData[attrEntityId] == category.origData[attrEntityId]) return true
        if (group.mappedData[attrPath] == category.origData[attrPath]) return true
        return false
    }

    companion object {
        private val attrEntityId = Attribute(
            "entity_id",
            AttributeStringType(false),
            false,
            true,
            true,
            null
        )
        val attrPath = Attribute(
            "__path",
            AttributeStringType(false),
            false,
            true,
            true,
            null
        )
    }
}
