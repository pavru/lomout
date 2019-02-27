package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
        transaction {
            SchemaUtils.run {
                statusLog.info("schema: start")
//                create(DbEntityTable)
                statusLog.info("schema: 1 finished")
//                create(
//                    EntityVarchars,
//                    EntityLongs,
//                    EntityBooleans,
//                    EntityDoubles,
//                    EntityDateTimes,
//                    EntityTexts
//                )
                statusLog.info("schema: 2 finished")
                create(PipelineSets)
                statusLog.info("schema: 3 finished")
            }
        }
    }
}
