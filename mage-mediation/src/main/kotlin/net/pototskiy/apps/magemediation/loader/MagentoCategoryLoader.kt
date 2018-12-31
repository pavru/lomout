package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageCategory

class MagentoCategoryLoader : AbstractLoader() {
    override val tableSet = MageCategory.Companion
}