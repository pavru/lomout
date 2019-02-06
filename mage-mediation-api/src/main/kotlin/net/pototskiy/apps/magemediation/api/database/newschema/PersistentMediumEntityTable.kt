package net.pototskiy.apps.magemediation.api.database.newschema

import net.pototskiy.apps.magemediation.api.database.mdtn.mediumDataState
import net.pototskiy.apps.magemediation.api.database.mdtn.mediumDataTarget

open class PersistentMediumEntityTable(table: String) : PersistentEntityTable(table) {
    val target = mediumDataTarget("target")
    val state = mediumDataState("state")
}
