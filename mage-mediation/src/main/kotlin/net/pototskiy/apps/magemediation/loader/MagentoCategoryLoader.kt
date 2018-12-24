package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageCategory
import net.pototskiy.apps.magemediation.database.mage.attribute.*

class MagentoCategoryLoader : AbstractLoader() {
    override val tableSet: TargetTableSet
        get() = TargetTableSet(
            MageCategory.Companion,
            MageCatInt.Companion,
            MageCatDouble.Companion,
            MageCatBool.Companion,
            MageCatVarchar.Companion,
            MageCatText.Companion,
            MageCatDate.Companion,
            MageCatDatetime.Companion
        )
}