package net.pototskiy.apps.magemediation.database

import com.mysql.cj.jdbc.MysqlDataSource
import net.pototskiy.apps.magemediation.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.DatabaseConfig
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.jetbrains.exposed.sql.Database
import java.util.*

fun initDatabase(config: DatabaseConfig, logLevel: Level = Level.ERROR) {
    Configurator.setLevel(EXPOSED_LOG_NAME, logLevel)
    val datasource = MysqlDataSource()
    datasource.setURL("jdbc:mysql://${config.server.host}:${config.server.port}/${config.name}")
    datasource.user = config.server.user
    datasource.password = config.server.password
    datasource.serverTimezone = TimeZone.getDefault().id

    Database.connect(datasource)
    DbSchema.createSchema()
}