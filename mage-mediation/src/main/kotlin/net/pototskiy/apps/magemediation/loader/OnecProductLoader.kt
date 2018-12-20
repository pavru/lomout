package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.attribute.*
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import net.pototskiy.apps.magemediation.database.onec.attribute.OnecProductBool
import net.pototskiy.apps.magemediation.database.onec.attribute.OnecProductBools

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
