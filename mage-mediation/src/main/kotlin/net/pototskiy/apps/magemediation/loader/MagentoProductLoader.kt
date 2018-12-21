package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageProduct
import net.pototskiy.apps.magemediation.database.mage.attribute.*

class MagentoProductLoader : AbstractLoader() {
    override val tableSet: TargetTableSet
        get() = TargetTableSet(
            MageProduct.Companion,
            MageProductInt.Companion,
            MageProductDouble.Companion,
            MageProductBool.Companion,
            MageProductVarchar.Companion,
            MageProductText.Companion,
            MageProductDate.Companion,
            MageProductDatetime.Companion
        )
}