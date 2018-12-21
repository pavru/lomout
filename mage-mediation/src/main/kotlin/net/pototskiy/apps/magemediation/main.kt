package net.pototskiy.apps.magemediation

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import net.pototskiy.apps.magemediation.database.initDatabase
import net.pototskiy.apps.magemediation.loader.DataLoader
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.joda.time.DateTime
import kotlin.contracts.ExperimentalContracts

const val LOG_NAME = "Import"
val IMPORT_DATETIME = DateTime()

@ExperimentalContracts
fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val jCommander = JCommander.Builder()
        .addObject(Args)
        .build()
    try {
        jCommander.parse(*args)
    } catch (e: ParameterException) {
        println(e.message)
        System.exit(1)
    }
    if (Args.help || Args.files.isEmpty()) {
        jCommander.usage()
    }
    setLogLevel()
    initDatabase()
    DataLoader.load()
}

fun setLogLevel() {
    val logger = Logger.getLogger(LOG_NAME)
    when(Args.logLevel) {
        "debug" -> logger.level = Level.DEBUG
        "info" -> logger.level = Level.INFO
        "warn" -> logger.level = Level.WARN
        "error" -> logger.level = Level.ERROR
        else -> logger.level = Level.WARN
    }
}

