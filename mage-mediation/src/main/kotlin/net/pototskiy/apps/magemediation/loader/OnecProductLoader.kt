package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.onec.OnecProduct

class OnecProductLoader : AbstractLoader() {
    override val tableSet = OnecProduct.Companion
}
