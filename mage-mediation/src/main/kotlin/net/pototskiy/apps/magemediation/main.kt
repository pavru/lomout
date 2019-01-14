package net.pototskiy.apps.magemediation

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import net.pototskiy.apps.magemediation.api.LOADER_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Configuration
import net.pototskiy.apps.magemediation.api.plugable.Plugable
import net.pototskiy.apps.magemediation.database.initDatabase
import net.pototskiy.apps.magemediation.loader.DataLoader
import net.pototskiy.apps.magemediation.mediator.MediatorFactory
import net.pototskiy.apps.magemediation.mediator.MediatorType
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import kotlin.contracts.ExperimentalContracts

lateinit var CONFIG: Configuration
lateinit var emptyPlugin: Plugable

@ExperimentalContracts
fun main(args: Array<String>) {
    val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
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

    statusLog.info("Application has started")

    CONFIG = Configuration(Args.configFile)

    // Set configuration for all plugins
    emptyPlugin = object : Plugable {}
    emptyPlugin.setPluginsConfig(CONFIG.config)

    setLogLevel()


    initDatabase(CONFIG.config.database)
    DataLoader.load(CONFIG.config)
    MediatorFactory.create(MediatorType.CATEGORY).merge()
    statusLog.info("Application has finished")
}

fun setLogLevel() {
    Configurator.setLevel(LOADER_LOG_NAME, Level.toLevel(Args.logLevel))
}

