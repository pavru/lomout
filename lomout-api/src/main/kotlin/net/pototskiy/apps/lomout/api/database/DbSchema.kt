package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

internal object DbSchema {
    fun createSchema(entityTypeManager: EntityTypeManager) {
        DbEntityTable.entityTypeManager = entityTypeManager
        transaction {
            SchemaUtils.create(DbEntityTable)
            SchemaUtils.create(
                EntityStrings,
                EntityLongs,
                EntityBooleans,
                EntityDoubles,
                EntityDateTimes,
                EntityDates,
                EntityTexts
            )
        }
    }
}
