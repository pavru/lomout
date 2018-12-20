package net.pototskiy.apps.magemediation.database

import com.mysql.cj.jdbc.MysqlDataSource
import net.pototskiy.apps.magemediation.config.Configuration
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.jetbrains.exposed.sql.Database
import java.util.*

fun initDatabase() {
    val config = Configuration.config.database
    val datasource = MysqlDataSource()
    datasource.setURL("jdbc:mysql://${config.server.host}:${config.server.port}/${config.name}")
    datasource.user = config.server.user
    datasource.password = config.server.password
    datasource.serverTimezone = TimeZone.getDefault().id

    Database.connect(datasource)
    val logger = Logger.getLogger("Exposed")
    logger.level = Level.ERROR
    DbSchema.createSchema()
}