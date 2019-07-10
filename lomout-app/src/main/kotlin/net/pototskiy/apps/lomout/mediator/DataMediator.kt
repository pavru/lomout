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

package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.values.secondFractions
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.*

object DataMediator {
    private const val DEFAULT_MAX_AGE = 10
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val log = LogManager.getLogger(PRINTER_LOG_NAME)
    private val processedRows = AtomicLong(0L)

    fun mediate(repository: EntityRepositoryInterface, config: Config) {
        val mediator = config.mediator ?: return
        statusLog.info(message("message.info.mediator.started"))
        val startTime = LocalDateTime.now()
        val orderedLines = mediator.lines.groupBy { it.outputEntity.qualifiedName }
        orderedLines.forEach { (_, lines) ->
            lines.forEach { line ->
                log.debug(message("message.debug.mediator.start_entity"), line.outputEntity.qualifiedName)
                val eType = line.outputEntity
                val rows = ProductionLineExecutor(repository).executeLine(line)
                repository.markEntitiesAsRemoved(eType)
                repository.updateAbsentDays(eType)
                repository.removeOldEntities(eType, DEFAULT_MAX_AGE)
                processedRows.addAndGet(rows)
                log.debug(message("message.debug.mediator.finish_entity"), line.outputEntity.qualifiedName)
            }
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).secondFractions
        statusLog.info(message("message.info.mediator.finished", duration, processedRows.get()))
    }
}
