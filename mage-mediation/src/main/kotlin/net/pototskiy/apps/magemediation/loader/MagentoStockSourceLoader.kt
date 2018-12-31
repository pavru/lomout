package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageStockSource

class MagentoStockSourceLoader : AbstractLoader() {
    override val tableSet = MageStockSource.Companion
}