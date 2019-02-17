package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.database.schema.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        transaction {
            SchemaUtils.run {
                create(SourceEntities)
                create(
                    SourceVarchars,
                    SourceLongs,
                    SourceBooleans,
                    SourceDoubles,
                    SourceDates,
                    SourceDateTimes,
                    SourceTexts
                )
                create(PipelineSets)
            }
        }
    }
}
