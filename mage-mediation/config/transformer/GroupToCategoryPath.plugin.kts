package transformer

import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin

public class GroupToCategoryPath : NewValueTransformPlugin<String, String>() {
    override var value: String = ""

    override fun execute(): String {
        return map[value] ?: value
    }

    companion object {
        private val map = mapOf(
            "/Root Catalog/Default Category/Каталог/Смазочные материалы (Hyundai, KIXX, GS OIL)" to
                    "/Root Catalog/Default Category/Каталог/Смазочные материалы"
        )
    }
}
