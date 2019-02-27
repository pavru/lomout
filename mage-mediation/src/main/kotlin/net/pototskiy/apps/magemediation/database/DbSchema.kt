package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.database.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        transaction {
            SchemaUtils.run {
                create(DbEntityTable)
                create(
                    EntityVarchars,
                    EntityLongs,
                    EntityBooleans,
                    EntityDoubles,
                    EntityDateTimes,
                    EntityTexts
                )
                create(PipelineSets)
            }
        }
    }
}
