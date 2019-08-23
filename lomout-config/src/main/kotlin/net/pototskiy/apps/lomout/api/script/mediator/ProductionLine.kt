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

package net.pototskiy.apps.lomout.api.script.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.suspectedLocation
import kotlin.reflect.KClass

/**
 * Mediator production line configuration
 *
 * @property outputEntity EntityType The output entity type
 * @constructor
 * @param inputEntities InputEntityCollection The input configuration
 * @param outputEntity EntityType The output entity type
 * @param pipeline Pipeline The root pipeline
 */
class ProductionLine<T : Document>(
    inputEntities: InputEntityCollection,
    val outputEntity: KClass<out Document>,
    pipeline: Pipeline<T>
) : AbstractLine(inputEntities, pipeline) {
    /**
     * Production line configuration builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property inputs InputEntityCollection? The input entities definition
     * @property output EntityType? The output entity type
     * @property pipeline Pipeline? The root pipeline
     * @constructor
     */
    @LomoutDsl
    class Builder<T : Document>(
        private val helper: ScriptBuildHelper,
        private var output: KClass<T>
    ) {
        private var inputs: InputEntityCollection? = null
        private var pipeline: Pipeline<T>? = null

        /**
         * Production line input definition
         *
         * ```
         * ...
         *  input {
         *      entity("entity type name") {
         *          filter {
         *          }
         *          extAttribute<Type>("new attr","source attr") {...}
         *          extAttribute<Type>("new attr","source attr") {...}
         *          ...
         *      }
         *      entity("entity type name") {...}
         *      entity("entity type name") {...}
         *      ...
         *  }
         * ...
         * ```
         * * [entity][InputEntityCollection.Builder.entity] — define input entity, **at least one must be defined**
         *
         * @param block The pipeline input definition
         */
        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            inputs = InputEntityCollection.Builder(helper).apply(block).build()
        }

        /**
         * Define root pipeline of line
         *
         * ```
         * ...
         *  pipeline {
         *      classifier {...}
         *      pipeline {...}
         *      assembler {...}
         *  }
         * ...
         * ```
         *
         * @param klass Array<out CLASS>
         * @param block The pipeline definition
         */
        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder<T>.() -> Unit
        ) {
            @Suppress("SpreadOperator")
            pipeline = Pipeline.Builder<T>(*klass).apply(block).build()
        }

        /**
         * Build production line configuration
         *
         * @return ProductionLine
         */
        @Suppress("ThrowsCount")
        fun build(): ProductionLine<T> {
            validatePipeline(
                pipeline
                    ?: throw AppConfigException(
                        suspectedLocation(),
                        message("message.error.script.pipeline.no_start_pipeline")
                    )
            )
            return ProductionLine(
                inputs ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.pipeline.input.one_must_be")
                ),
                output,
                pipeline!!
            )
        }

        private fun validatePipeline(pipeline: Pipeline<T>) {
            if (pipeline.pipelines.isEmpty() && pipeline.assembler == null) {
                throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.pipeline.matched.must_have_assembler")
                )
            }
            for (line in pipeline.pipelines) validatePipeline(line)
        }
    }
}
