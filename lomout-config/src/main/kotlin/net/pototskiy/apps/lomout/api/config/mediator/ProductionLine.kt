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
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
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
class ProductionLine(
    inputEntities: InputEntityCollection,
    val outputEntity: KClass<out Document>,
    pipeline: Pipeline
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
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var inputs: InputEntityCollection? = null
        private var output: KClass<out Document>? = null
        private var pipeline: Pipeline? = null

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
            block: Pipeline.Builder.() -> Unit
        ) {
            @Suppress("SpreadOperator")
            pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        /**
         * Define production line output as reference to existing entity type
         *
         * ```
         * ...
         *  output(class)
         * ...
         * ```
         *
         * @param entityType String The already defined entity type name
         */
        @PublicApi
        fun output(entityType: KClass<out Document>) {
            output = entityType
        }

        /**
         * Build production line configuration
         *
         * @return ProductionLine
         */
        @Suppress("ThrowsCount")
        fun build(): ProductionLine {
            validatePipeline(
                pipeline
                    ?: throw AppConfigException(
                        suspectedLocation(),
                        message("message.error.config.pipeline.no_start_pipeline")
                    )
            )
            return ProductionLine(
                inputs ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.config.pipeline.input.one_must_be")
                ),
                output ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.config.pipeline.output.must_be")
                ),
                pipeline!!
            )
        }

        private fun validatePipeline(pipeline: Pipeline) {
            if (pipeline.pipelines.isEmpty() && pipeline.assembler == null) {
                throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.config.pipeline.matched.must_have_assembler")
                )
            }
            for (line in pipeline.pipelines) validatePipeline(line)
        }
    }
}
