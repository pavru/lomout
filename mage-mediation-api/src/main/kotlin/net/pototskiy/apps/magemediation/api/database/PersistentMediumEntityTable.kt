package net.pototskiy.apps.magemediation.api.database

open class PersistentMediumEntityTable(table: String) : PersistentEntityTable(table) {
    val target = mediumDataTarget("target")
    val state = mediumDataState("state")
}
