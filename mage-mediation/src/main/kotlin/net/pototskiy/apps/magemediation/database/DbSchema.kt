package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.DEBUG)
        transaction {
                statusLog.info("schema: start")
                SchemaUtils.create(DbEntityTable)
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
                SchemaUtils.create(PipelineSets)
                statusLog.info("schema: 3 finished")
        }
    }
}
