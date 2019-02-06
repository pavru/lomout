package net.pototskiy.apps.magemediation

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import net.pototskiy.apps.magemediation.api.ROOT_LOG_NAME
import net.pototskiy.apps.magemediation.api.STATUS_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigurationBuilderFromDSL
import net.pototskiy.apps.magemediation.api.database.newschema.EntityClass
import net.pototskiy.apps.magemediation.database.initDatabase
import net.pototskiy.apps.magemediation.loader.DataLoader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import java.io.File
import kotlin.contracts.ExperimentalContracts


lateinit var CONFIG_BUILDER: ConfigurationBuilderFromDSL

@ExperimentalContracts
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

    Config.Builder.initConfigBuilder()
    CONFIG_BUILDER = ConfigurationBuilderFromDSL(File(Args.configFile))
    initDatabase(CONFIG_BUILDER.config.database)
    EntityClass.initEntityCLassRegistrator()

    // Configure plugins
    @Suppress("UNUSED_VARIABLE") val plugable = PluginConfiguration()

    DataLoader.load(CONFIG_BUILDER.config)
//    MediatorFactory.create(MediatorType.CATEGORY).merge()
    statusLog.info("Application has finished")
}

fun setLogLevel() {
    Configurator.setLevel(ROOT_LOG_NAME,Level.toLevel(Args.logLevel))
}

