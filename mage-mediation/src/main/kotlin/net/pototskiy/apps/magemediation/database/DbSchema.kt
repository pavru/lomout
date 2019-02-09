package net.pototskiy.apps.magemediation.database

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
                create(MediumEntities)
                create(
                    MediumVarchars,
                    MediumLongs,
                    MediumBooleans,
                    MediumDoubles,
                    MediumDates,
                    MediumDateTimes,
                    MediumTexts
                )
                create(MediumTmpMatches)
            }
        }
    }
}
