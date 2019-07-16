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

package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.suspectedLocation

/**
 * Mediator configuration
 *
 * @property lines ProductionLineCollection The production lines  collection
 * @constructor
 */
data class MediatorConfiguration(
    val lines: ProductionLineCollection
) {
    /**
     * Mediator configuration builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property lines MutableList<ProductionLine> The production lines collection
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var lines = mutableListOf<ProductionLine>()

        /**
         * Define production line
         *
         * ```
         * ...
         *  crossProductionLine {
         *      input {...}
         *      output {...}
         *      pipeline {...}
         *  }
         * ...
         * ```
         * * [input][ProductionLine.Builder.input] — define input entities, **mandatory**
         * * [output][ProductionLine.Builder.output] — define new output entity or reference to existing, **mandatory**
         * * [pipeline][ProductionLine.Builder.pipeline] — define root pipeline of production line, **mandatory**
         *
         * @param block The production line definition
         */
        fun productionLine(block: ProductionLine.Builder.() -> Unit) {
            lines.add(ProductionLine.Builder(helper).apply(block).build())
        }

        /**
         * Build mediator configuration
         * @return MediatorConfiguration
         */
        fun build(): MediatorConfiguration {
            checkCycling()
            return MediatorConfiguration(ProductionLineCollection(lines))
        }

        private fun checkCycling() {
            val visitedLines = mutableMapOf<ProductionLine, Boolean>()
            fun visitChain(line: ProductionLine) {
                if (visitedLines[line] == true) {
                    throw AppConfigException(
                        suspectedLocation(),
                        MessageBundle.message("message.error.config.pipeline.line_cycling")
                    )
                }
                visitedLines[line] = true
                line.inputEntities.forEach { entity ->
                    lines.findLast { it.outputEntity == entity.entity }?.let { visitChain(it) }
                }
            }
            lines.forEach {
                visitedLines.clear()
                visitChain(it)
            }
        }
    }
}
