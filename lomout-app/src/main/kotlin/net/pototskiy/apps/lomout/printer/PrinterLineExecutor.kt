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

import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.printer.PrinterLine
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.mediator.LineExecutor
import net.pototskiy.apps.lomout.mediator.PipelineExecutor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class PrinterLineExecutor(repository: EntityRepositoryInterface) : LineExecutor(repository) {

    override val logger: Logger = LogManager.getLogger(PRINTER_LOG_NAME)
    private lateinit var printer: EntityPrinter

    override fun processResultData(data: Map<Attribute, Any>): Long =
        if (data.isEmpty()) {
            0L
        } else {
            printer.print(data)
        }

    override fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor = PipelineExecutor(
        line.pipeline,
        line.inputEntities,
        line.inputEntities.first().entity
    )

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    override fun executeLine(line: AbstractLine): Long {
        line as PrinterLine
        val entityPrinter = EntityPrinter(
            line.outputFieldSets.file,
            line.outputFieldSets.fieldSets,
            line.outputFieldSets.printHead
        )
        entityPrinter.use {
            this.printer = it
            super.executeLine(line)
        }
        return processedRows
    }
}
