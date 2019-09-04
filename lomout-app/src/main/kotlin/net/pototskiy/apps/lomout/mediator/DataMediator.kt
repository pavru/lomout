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
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.STATUS_LOG_NAME
import net.pototskiy.apps.lomout.api.entity.values.secondWithFractions
import net.pototskiy.apps.lomout.api.errorMessageFromException
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.mediator.ProductionLine
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.*

class DataMediator {
    private val statusLog = LogManager.getLogger(STATUS_LOG_NAME)
    private val logger = LogManager.getLogger(PRINTER_LOG_NAME)
    private val processedRows = AtomicLong(0L)

    fun mediate() {
        val context = LomoutContext.getContext()
        context.script.mediator ?: return
        statusLog.info(message("message.info.mediator.started"))
        val startTime = LocalDateTime.now()
        sortProductionLines(context.script).forEach { line ->
            logger.debug(message("message.debug.mediator.start_entity"), line.outputEntity.qualifiedName)
            val eType = line.outputEntity
            @Suppress("TooGenericExceptionCaught")
            val rows = try {
                ProductionLineExecutor(context).executeLine(line)
            } catch (e: Exception) {
                AppDataException(
                    suspectedLocation(),
                    message("message.error.mediator.common_error"),
                    e
                ).errorMessageFromException(logger)
                0L
            }
            context.repository.markEntitiesAsRemoved(eType)
            context.repository.updateAbsentDays(eType)
            context.repository.removeOldEntities(eType, DEFAULT_MAX_AGE)
            processedRows.addAndGet(rows)
            logger.debug(message("message.debug.mediator.finish_entity"), line.outputEntity.qualifiedName)
        }
        val duration = Duration.between(startTime, LocalDateTime.now()).secondWithFractions
        statusLog.info(message("message.info.mediator.finished", duration, processedRows.get()))
    }

    private fun sortProductionLines(lomoutScript: LomoutScript): List<ProductionLine<*>> {
        val chains = mutableListOf<List<ProductionLine<*>>>()
        fun buildCain(line: ProductionLine<*>): List<ProductionLine<*>> {
            val chain = mutableListOf(line)
            line.inputEntities.forEach { inputEntity ->
                lomoutScript.mediator?.lines?.find { it.outputEntity == inputEntity.entity }?.let {
                    chain.addAll(buildCain(it))
                }
            }
            return chain
        }
        lomoutScript.mediator?.lines?.forEach {
            chains.add(buildCain(it).reversed())
        }
        val sorted = mutableListOf<ProductionLine<*>>()
        chains.sortedBy { it.size }.forEach { list ->
            list.forEach { if (!sorted.contains(it)) sorted.add(it) }
        }
        return sorted
    }

    companion object {
        private const val DEFAULT_MAX_AGE = 10
    }
}
