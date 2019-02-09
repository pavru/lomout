package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.ENTITY_TYPE_NAME_LENGTH
import org.jetbrains.exposed.dao.IntIdTable

abstract class PersistentEntityTable(table: String) : IntIdTable(table) {
    val entityType = varchar("entity_type", ENTITY_TYPE_NAME_LENGTH).index()
}
