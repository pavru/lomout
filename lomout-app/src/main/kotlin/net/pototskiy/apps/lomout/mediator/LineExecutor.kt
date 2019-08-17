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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.MessageBundle.message
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.pipeline.ClassifierElement
import net.pototskiy.apps.lomout.api.document.DocumentData
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.errorMessageFromException
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.logging.log4j.Logger

abstract class LineExecutor(protected val repository: EntityRepositoryInterface) {
    private lateinit var line: AbstractLine

    protected abstract val logger: Logger
    private val jobs = mutableListOf<Job>()
    protected var processedRows = 0L

    abstract fun processResultData(data: DocumentData): Long
    abstract fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    open fun executeLine(line: AbstractLine): Long {
        this.line = line
        processedRows = 0L
        try {
            runBlocking {
                val pipeline = preparePipelineExecutor(line)
                val inputChannel: Channel<ClassifierElement> = Channel()
                jobs.add(launch(Dispatchers.IO) {
                    pipeline.execute(inputChannel).consumeEach {
                        try {
                            processedRows += processResultData(it)
                        } catch (e: AppException) {
                            AppDataException(
                                suspectedLocation(),
                                message("message.error.mediator.cannot_process_entity"),
                                e
                            ).errorMessageFromException(logger)
                        }
                    }
                })
                topLevelInput(line).forEach { inputChannel.send(it) }
                inputChannel.close()
                joinAll(*jobs.toTypedArray())
            }
        } catch (e: Exception) {
            e.errorMessageFromException(logger)
        }
        return processedRows
    }

    private suspend fun topLevelInput(line: AbstractLine) =
        sequence<ClassifierElement> {
            line.inputEntities.forEach { input ->
                var pageNumber = 0
                do {
                    val items = repository.getIDs(input.entity, PAGE_SIZE, pageNumber, input.includeDeleted)
                    items.forEach {
                        yield(ClassifierElement.Mismatched(repository.get(input.entity, it, input.includeDeleted)!!))
                    }
                    pageNumber++
                } while (items.isNotEmpty())
            }
        }

    companion object {
        const val PAGE_SIZE = 1000
    }
}
