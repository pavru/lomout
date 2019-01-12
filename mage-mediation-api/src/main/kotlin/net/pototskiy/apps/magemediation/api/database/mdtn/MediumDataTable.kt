package net.pototskiy.apps.magemediation.api.database.mdtn

import org.jetbrains.exposed.dao.IntIdTable

abstract class MediumDataTable(table: String): IntIdTable(table) {
    val target = mediumDataTarget("target")
    val state = mediumDataState("state")
}