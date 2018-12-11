package database

import com.mysql.cj.jdbc.MysqlDataSource
import configuration.Config
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.jetbrains.exposed.sql.Database
import java.util.*

fun initDatabase() {
    val config = Config.config
    val datasource = MysqlDataSource()
    datasource.setURL("jdbc:mysql://${config.database.server.host}:${config.database.server.port}/${config.database.name}")
    datasource.user = config.database.user
    datasource.password = config.database.password
    datasource.serverTimezone = TimeZone.getDefault().id

    Database.connect(datasource)
    val logger = Logger.getLogger("Exposed")
    logger.level = Level.ERROR
    DbSchema.createSchema()
}