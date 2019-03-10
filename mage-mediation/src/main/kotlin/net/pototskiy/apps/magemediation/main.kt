package net.pototskiy.apps.magemediation

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.magemediation.api.ROOT_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.ConfigurationBuilderFromDSL
import net.pototskiy.apps.magemediation.api.plugable.PluginContext
import net.pototskiy.apps.magemediation.database.initDatabase
import net.pototskiy.apps.magemediation.loader.DataLoader
import net.pototskiy.apps.magemediation.mediator.DataMediator
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import java.io.File

lateinit var CONFIG_BUILDER: ConfigurationBuilderFromDSL

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    val jCommander = JCommander.Builder()
        .addObject(Args)
        .build()
    try {
        jCommander.parse(*args)
    } catch (e: ParameterException) {
        System.exit(1)
        return
    }
    if (Args.help) {
        jCommander.usage()
        System.exit(1)
    }
    setLogLevel()

    statusLog.info("Application has started")

    CONFIG_BUILDER = ConfigurationBuilderFromDSL(File(Args.configFile))
    setupPluginContext()
    initDatabase(CONFIG_BUILDER.config.database, CONFIG_BUILDER.config.entityTypeManager)
    DataLoader.load(CONFIG_BUILDER.config)
    DataMediator.mediate(CONFIG_BUILDER.config)
//    MediatorFactory.create(MediatorType.CATEGORY).merge()
    statusLog.info("Application has finished")
}

fun setLogLevel() {
    Configurator.setLevel(ROOT_LOG_NAME, Level.toLevel(Args.logLevel))
}

fun setupPluginContext() {
    PluginContext.config = CONFIG_BUILDER.config
    PluginContext.entityTypeManager = CONFIG_BUILDER.config.entityTypeManager
}
