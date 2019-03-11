package net.pototskiy.apps.magemediation.database

import com.mysql.cj.jdbc.MysqlDataSource
import net.pototskiy.apps.magemediation.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.DatabaseConfig
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.jetbrains.exposed.sql.Database
import java.util.*

fun initDatabase(
    config: DatabaseConfig,
    entityTypeManager: EntityTypeManager,
    logLevel: Level = Level.ERROR
) {
    val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    Configurator.setLevel(EXPOSED_LOG_NAME, logLevel)
    statusLog.info("Database has stated to check and init")
    val datasource = MysqlDataSource()
    datasource.setURL("jdbc:mysql://${config.server.host}:${config.server.port}/${config.name}")
    datasource.user = config.server.user
    datasource.password = config.server.password
    datasource.serverTimezone = TimeZone.getDefault().id

    @Suppress("TooGenericExceptionCaught")
    try {
        val db = Database.connect(datasource)
        statusLog.info("DB dialect: ${db.dialect.name}")
        DbSchema.createSchema(entityTypeManager)
    } catch (e: Exception) {
        statusLog.error("Can not init DB", e)
        System.exit(1)
    }
    statusLog.info("Database has finished to check and init")
}
