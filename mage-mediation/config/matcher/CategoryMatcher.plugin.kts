package matcher

import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherPlugin

public class CategoryMatcher : EntityMatcherPlugin() {
    override fun execute(): Boolean {
        return entities.size == 4
    }
}
