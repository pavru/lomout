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

import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.script.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.script.mediator.ProductionLine
import net.pototskiy.apps.lomout.api.document.DocumentData
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.loader.EntityUpdater
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ProductionLineExecutor(repository: EntityRepositoryInterface) : LineExecutor(repository) {

    override val logger: Logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    private lateinit var entityUpdater: EntityUpdater

    override fun processResultData(data: DocumentData): Long =
        if (data.isEmpty()) {
            0L
        } else {
            entityUpdater.update(data)
        }

    override fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor {
        line as ProductionLine<*>
        return PipelineExecutor(
            line.pipeline,
            line.inputEntities,
            line.outputEntity
        )
    }

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    override fun executeLine(line: AbstractLine): Long {
        line as ProductionLine<*>
        val targetEntityType = line.outputEntity
        entityUpdater = EntityUpdater(repository, targetEntityType)
        return super.executeLine(line)
    }
}
