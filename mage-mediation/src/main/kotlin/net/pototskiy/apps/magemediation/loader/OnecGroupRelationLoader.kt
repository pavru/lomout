package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.onec.OnecGroupRelation

class OnecGroupRelationLoader : AbstractLoader() {
    override val tableSet = OnecGroupRelation.Companion
}
