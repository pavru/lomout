package net.pototskiy.apps.magemediation.api.database

import org.jetbrains.exposed.dao.IntIdTable

abstract class PersistentEntityTable(table: String) : IntIdTable(table) {
    val entityType = varchar("entity_type",100).index()
}
