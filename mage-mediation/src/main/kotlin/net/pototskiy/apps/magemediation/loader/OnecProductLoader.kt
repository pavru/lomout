package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.attribute.*

class OnecProductLoader : AbstractLoader() {
    override val tableSet
        get() = TargetTableSet(
            OnecProduct.Companion,
            OnecProductInt.Companion,
            OnecProductDouble.Companion,
            OnecProductBool.Companion,
            OnecProductVarchar.Companion,
            OnecProductText.Companion,
            OnecProductDate.Companion,
            OnecProductDatetime.Companion
        )
}
