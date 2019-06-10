package net.pototskiy.apps.lomout

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.ConfigurationBuilderFromDSL
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.database.initDatabase
import net.pototskiy.apps.lomout.jcommander.CommandHelp
import net.pototskiy.apps.lomout.jcommander.CommandMain
import net.pototskiy.apps.lomout.jcommander.CommandVersion
import net.pototskiy.apps.lomout.loader.DataLoader
import net.pototskiy.apps.lomout.mediator.DataMediator
import net.pototskiy.apps.lomout.printer.DataPrinter
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.joda.time.DateTime
import org.joda.time.Duration
import java.io.File

lateinit var CONFIG_BUILDER: ConfigurationBuilderFromDSL

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    val mainCommand = CommandMain()
    val jCommander = JCommander.Builder()
        .addCommand(CommandHelp())
        .addCommand(CommandVersion())
        .addCommand(mainCommand)
        .build()
    try {
        @Suppress("SpreadOperator")
        jCommander.parse(*args)
    } catch (e: ParameterException) {
        println(e.message)
        jCommander.usage()
        System.exit(1)
    }
    if (jCommander.parsedCommand == "--help") {
        jCommander.usage()
        return
    } else if (jCommander.parsedCommand == "--version") {
        println("LoMout v${BuildInfo.lomoutVersion}")
        return
    }
    setLogLevel(mainCommand)

    val startTime = DateTime()
    statusLog.info("Application has started")

    CONFIG_BUILDER = ConfigurationBuilderFromDSL(
        File(mainCommand.configFile.first()),
        mainCommand.scriptCacheDir,
        mainCommand.doNotUseScriptCache
    )
    setupPluginContext(File(mainCommand.configFile.first()))
    initDatabase(
        CONFIG_BUILDER.config.database,
        CONFIG_BUILDER.config.entityTypeManager,
        Level.toLevel(mainCommand.sqlLogLevel)
    )
    PluginContext.logger = LogManager.getLogger(LOADER_LOG_NAME)
    CONFIG_BUILDER.config.loader?.let { DataLoader.load(CONFIG_BUILDER.config) }
    PluginContext.logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    CONFIG_BUILDER.config.mediator?.let { DataMediator.mediate(CONFIG_BUILDER.config) }
    PluginContext.logger = LogManager.getLogger(PRINTER_LOG_NAME)
    CONFIG_BUILDER.config.printer?.let { DataPrinter.print(CONFIG_BUILDER.config) }
//    MediatorFactory.create(MediatorType.CATEGORY).merge()
    val duration = Duration(startTime, DateTime()).standardSeconds
    statusLog.info("Application has finished, duration: ${duration}s")
}

/**
 * Set root log level from command line args
 */
fun setLogLevel(command: CommandMain) {
    Configurator.setLevel(ROOT_LOG_NAME, Level.toLevel(command.logLevel))
}

/**
 * Set plugin context
 */
fun setupPluginContext(scriptFile: File) {
    PluginContext.config = CONFIG_BUILDER.config
    PluginContext.entityTypeManager = CONFIG_BUILDER.config.entityTypeManager
    PluginContext.scriptFile = scriptFile
}
