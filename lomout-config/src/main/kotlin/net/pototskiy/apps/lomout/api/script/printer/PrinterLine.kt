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

package net.pototskiy.apps.lomout.api.script.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.script.mediator.InputEntityCollection
import net.pototskiy.apps.lomout.api.script.mediator.Pipeline
import net.pototskiy.apps.lomout.api.suspectedLocation
import kotlin.reflect.KClass

/**
 * Printer line
 *
 * @property outputFieldSets PrinterOutput
 * @constructor
 * @param inputEntities InputEntityCollection The input entities collection
 * @param outputFieldSets PrinterOutput The printer line output
 * @param pipeline Pipeline The printer line pipeline
 */
class PrinterLine<T : Document>(
    inputEntities: InputEntityCollection,
    val outputFieldSets: PrinterOutput,
    pipeline: Pipeline<T>
) : AbstractLine(inputEntities, pipeline) {
    /**
     * Printer line builder class
     *
     * @property helper ConfigBuildHelper The configuration builder helper
     * @property inputs InputEntityCollection? The input entities collection
     * @property pipeline Pipeline? The printer line pipeline
     * @property outputs PrinterOutput? The printer line output
     * @constructor
     * @param helper ConfigBuilderHelper The config builder helper
     */
    @LomoutDsl
    class Builder<T : Document>(
        private val helper: ScriptBuildHelper,
        private val entityType: KClass<T>
    ) {
        private var inputs: InputEntityCollection? = null
        private var pipeline: Pipeline<T>? = null
        private var outputs: PrinterOutput? = null

        /**
         * Define inputs for printer line
         *
         * ```
         * ...
         *  input {
         *      entity(class) {
         *          includeDeleted()
         *      }
         *      entity("entity_type") {
         *
         *      }
         *  }
         * ...
         * ```
         * * entity — define entity for line input, **at least one entity must be defined**
         * * class — entity type class
         *
         * Printer line generate stream of entity from all defined inputs like SQL UNION
         *
         * @param block The pipeline input definition
         */
        @LomoutDsl
        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            this.inputs = InputEntityCollection.Builder(helper).also(block).build()
            this.inputs?.let {
                if (it.size != 1) {
                    throw AppConfigException(
                        suspectedLocation(),
                        message("message.error.script.print.input_entity")
                    )
                }
            }
        }

        /**
         * Define print line output
         *
         * ```
         * ...
         *  output {
         *      file {...}
         *      printHead = true
         *      outputFields {
         *          main("set name") {
         *              field("field name")
         *              ...
         *          }
         *          extra("set_name") {
         *              field("field name")
         *              ...
         *          }
         *      }
         *  }
         * ...
         * ```
         * * [file][net.pototskiy.apps.lomout.api.script.loader.SourceData.Builder] —
         *      define output file, **mandatory**
         * * printHead: Boolean — print headers in first row, *optional, default: true*
         * * [outputFields][net.pototskiy.apps.lomout.api.script.loader.FieldSetCollection.Builder] —
         *      define fields to print, **mandatory**
         *
         * @param block The output definition
         */
        @LomoutDsl
        fun output(block: PrinterOutput.Builder<T>.() -> Unit) {
            this.outputs = PrinterOutput.Builder(helper, entityType).apply(block).build()
        }

        /**
         * Define printer pipeline to process entities, this is root element of the pipeline tree
         *
         * ```
         * ...
         *  pipeline {
         *      classifier {...}
         *      classifier<ClassifierPluginClass>()
         *      pipeline(Pipeline.CLASS.MATCHED) {
         *          assembler { target, entities ->
         *              // assembler code
         *          }
         *          assembler<AssemblerPluginClass> {
         *              // assembler options, it depends on assembler class
         *          }
         *      }
         *      pipeline(Pipeline.CLASS.UNMATCHED) {
         *          classifier {...}
         *          pipeline(Pipeline.CLASS.MATCHED) {
         *              classifier {...}
         *              pipeline(Pipeline.CLASS.MATCHED) {
         *                  assembler {...}
         *              }
         *              pipeline(Pipeline.CLASS.UNMATCHED) {
         *                  assembler {...}
         *              }
         *          }
         *          pipeline(Pipeline.CLASS.UNMATCHED) {
         *              assembler {...}
         *          }
         *      }
         *  }
         * ...
         * ```
         * * [classifier][Pipeline.Builder.classifier] — entity classifier, it splits input of pipeline into 2 streams:
         *      matched entities and unmatched entities. This is *optional* component, **only one classifier is allowed
         *      per pipeline**. If classifier is omitted all entities go to assembler.
         * * [assembler][Pipeline.Builder.assembler] — entity assembler, it prepares target entity attribute as map.
         *      If pipeline has not child pipeline it must have assembler.
         * * [pipeline][Pipeline.Builder.pipeline] — child pipeline, parameter indicates for which entities (matched,
         *      unmatched) pipeline must be applied
         *
         * @param klass Array<out CLASS>
         * @param block The pipeline definition
         */
        @LomoutDsl
        @Suppress("SpreadOperator")
        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder<T>.() -> Unit
        ) {
            this.pipeline = Pipeline.Builder<T>(*klass).apply(block).build()
        }

        /**
         * Build printer line
         *
         * @return PrinterLine
         */
        @Suppress("ThrowsCount")
        fun build(): PrinterLine<T> {
            validatePipeline(
                pipeline ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.pipeline.no_start_pipeline")
                )
            )
            return PrinterLine(
                inputs ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.print.input_entity")
                ),
                outputs ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.pipeline.output.must_be")
                ),
                pipeline ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.pipeline.no_start_pipeline")
                )
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
