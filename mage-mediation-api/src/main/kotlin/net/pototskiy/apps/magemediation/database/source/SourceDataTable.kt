package net.pototskiy.apps.magemediation.database.source

import org.jetbrains.exposed.dao.IntIdTable

abstract class SourceDataTable(table: String): IntIdTable(table) {
    val createdInMedium = datetime("created_in_medium").index()
    val updatedInMedium = datetime("updated_in_medium").index()
    val absentDays = integer("absent_days").index()
}
