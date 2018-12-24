package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageAdvPrice

class MagentoAdvPriceLoader : AbstractLoader() {
    override val tableSet = TargetTableSet(
        MageAdvPrice.Companion
    )
}