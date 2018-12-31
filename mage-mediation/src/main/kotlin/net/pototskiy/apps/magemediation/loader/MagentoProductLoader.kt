package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageProduct

class MagentoProductLoader : AbstractLoader() {
    override val tableSet = MageProduct.Companion
}