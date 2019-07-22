/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.ConfigurationBuilderFromDSL
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.jcommander.CommandHelp
import net.pototskiy.apps.lomout.jcommander.CommandMain
import net.pototskiy.apps.lomout.jcommander.CommandVersion
import net.pototskiy.apps.lomout.loader.DataLoader
import net.pototskiy.apps.lomout.mediator.DataMediator
import net.pototskiy.apps.lomout.printer.DataPrinter
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import kotlin.system.exitProcess

lateinit var CONFIG_BUILDER: ConfigurationBuilderFromDSL

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@Suppress("ReturnCount")
fun main(args: Array<String>) {
    val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    val mainCommand = CommandMain()
    if (parseArguments(mainCommand, args)) return
    setLogLevel(mainCommand)

    val startTime = LocalDateTime.now()
    statusLog.info(message("message.info.app.started"))
    if (!File(mainCommand.configFile.first()).exists()) {
        statusLog.error(message("message.error.app.config_not_found"), mainCommand.configFile.first())
        exitProcess(1)
    }
    CONFIG_BUILDER = ConfigurationBuilderFromDSL(
        File(mainCommand.configFile.first()),
        mainCommand.scriptCacheDir,
        mainCommand.doNotUseScriptCache
    )

    val repository = EntityRepository(
        CONFIG_BUILDER.config.database,
        Level.toLevel(mainCommand.sqlLogLevel)
    )
    setupPluginContext(File(mainCommand.configFile.first()))
    PluginContext.logger = LogManager.getLogger(LOADER_LOG_NAME)
    PluginContext.repository = repository

    CONFIG_BUILDER.config.loader?.let { DataLoader.load(repository, CONFIG_BUILDER.config) }
    PluginContext.logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    CONFIG_BUILDER.config.mediator?.let { DataMediator.mediate(repository, CONFIG_BUILDER.config) }
    PluginContext.logger = LogManager.getLogger(PRINTER_LOG_NAME)
    CONFIG_BUILDER.config.printer?.let { DataPrinter.print(repository, CONFIG_BUILDER.config) }
//    MediatorFactory.create(MediatorType.CATEGORY).merge()
    val duration = Duration.between(startTime, LocalDateTime.now()).seconds
    statusLog.info(message("message.info.app.finished", duration))
}

@Suppress("ReturnCount")
private fun parseArguments(
    mainCommand: CommandMain,
    args: Array<String>
): Boolean {
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
        exitProcess(1)
    }
    if (jCommander.parsedCommand == "--help") {
        jCommander.usage()
        return true
    } else if (jCommander.parsedCommand == "--version") {
        println("LoMout v${BuildInfo.lomoutVersion}")
        return true
    }
    return false
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
    PluginContext.scriptFile = scriptFile
}