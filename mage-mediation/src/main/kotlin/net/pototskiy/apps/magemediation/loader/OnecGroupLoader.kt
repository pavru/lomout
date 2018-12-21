package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.onec.OnecGroup

class OnecGroupLoader : AbstractLoader() {
    override val tableSet
        get() = TargetTableSet(
            OnecGroup.Companion,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
}
