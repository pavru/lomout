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

package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.values.secondFractions
import net.pototskiy.apps.lomout.api.errorMessageFromException
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.*

object DataPrinter {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val printedRows = AtomicLong(0L)
    private val logger = LogManager.getLogger(PRINTER_LOG_NAME)

    fun print(repository: EntityRepositoryInterface, config: Config) {
        val printer = config.printer ?: return
        statusLog.info(message("message.info.printer.started"))
        val startTime = LocalDateTime.now()
        val orderedLines = printer.lines.groupBy { it.outputFieldSets.file.file.id }
        orderedLines.forEach { (_, lines) ->
            lines.forEach { line ->
                logger.debug(message("message.debug.property.start_file", line.outputFieldSets.file.file.file.name))
                @Suppress("TooGenericExceptionCaught")
                val rows = try {
                    PrinterLineExecutor(repository).executeLine(line)
                } catch (e: Exception) {
                    AppDataException(
                        suspectedLocation(),
                        message("message.error.printer.common_error"),
                        e
                    ).errorMessageFromException(logger)
                    0L
                }
                printedRows.addAndGet(rows)
                logger.debug(message("message.debug.printer.finish_file", line.outputFieldSets.file.file.file.name))
            }
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).secondFractions
        statusLog.info(message("message.info.printer.finished", duration, printedRows.get()))
    }
}
