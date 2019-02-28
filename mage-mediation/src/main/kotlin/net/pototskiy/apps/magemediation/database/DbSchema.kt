package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.database.*
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
        transaction {
                SchemaUtils.create(DbEntityTable)
                SchemaUtils.create(
                    EntityVarchars,
                    EntityLongs,
                    EntityBooleans,
                    EntityDoubles,
                    EntityDateTimes,
                    EntityTexts
                )
                SchemaUtils.create(PipelineSets)
        }
    }
}
