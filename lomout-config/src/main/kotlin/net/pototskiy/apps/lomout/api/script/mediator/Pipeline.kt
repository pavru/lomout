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

import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierPlugin
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.script.pipeline.PipelineClassifier
import net.pototskiy.apps.lomout.api.script.pipeline.PipelineClassifierWithFunction
import net.pototskiy.apps.lomout.api.script.pipeline.PipelineClassifierWithPlugin
import java.util.*
import kotlin.reflect.KClass

/**
 * Pipeline configuration
 *
 * @property dataClass The pipeline class, which entities are accepted
 * @property classifier The pipeline classifier
 * @property pipelines The child pipelines
 * @property assembler The pipeline assembler
 * @property pipelineID The internal unique pipeline id
 * @constructor
 */
data class Pipeline<T : Document>(
    val dataClass: List<CLASS>,
    val classifier: PipelineClassifier,
    val pipelines: List<Pipeline<T>>,
    val assembler: PipelineAssembler<T>?
) {
    /**
     * Internal unique pipeline id
     */
    private val pipelineID = UUID.randomUUID().toString()

    /**
     * Pipeline class
     */
    enum class CLASS {
        /**
         * Pipeline accepts matched entities
         */
        MATCHED,
        /**
         * Pipeline accepts unmatched entities
         */
        UNMATCHED
    }

    /**
     * Test if pipeline can be applied to class (matched, unmatched) of entities
     *
     * @param klass Array<out CLASS>
     * @return Boolean
     */
    @PublicApi
    fun isApplicablePipeline(vararg klass: CLASS): Boolean = dataClass.containsAll(klass.toList())

    /**
     * Pipeline configuration builder class
     *
     * @property dataClass List<CLASS> The pipeline accepted class
     * @property classifier PipelineClassifier? The pipeline classifier
     * @property assembler PipelineAssembler? The pipeline assembler
     * @property pipelines MutableList<Pipeline> The child pipelines
     * @constructor
     */
    @LomoutDsl
    class Builder<T : Document>(vararg klass: CLASS) {
        private val dataClass = klass.toList()
        /**
         * Pipeline classifier, **do not use in DSL**
         */
        var classifier: PipelineClassifier? = null
        /**
         * Pipeline assembler, **do not use in DSL**
         */
        var assembler: PipelineAssembler<T>? = null
        private var pipelines = mutableListOf<Pipeline<T>>()

        /**
         * Inline classifier
         *
         * ```
         * ...
         *  classifier { entities ->
         *      // classifier code
         *  }
         * ...
         * ```
         * entities — pipeline input entities
         *
         * @param block PipelineClassifierFunction
         */
        @JvmName("classifier__function")
        @PublicApi
        fun classifier(block: PipelineClassifierFunction) {
            classifier = PipelineClassifierWithFunction(block)
        }

        /**
         * Pipeline classifier with a plugin
         *
         * ```
         * ...
         *  classifier<ClassifierPluginClass> {
         *      // classifier options, it depends on plugin class
         *  }
         * ...
         * ```
         * [ClassifierPluginClass][net.pototskiy.apps.lomout.api.plugable.PipelineClassifierPlugin] — classifier
         *      plugin class
         *
         * @param block The classifier options
         */
        @JvmName("classifier__plugin")
        inline fun <reified P : PipelineClassifierPlugin> classifier(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            classifier = PipelineClassifierWithPlugin(P::class, block as (PipelineClassifierPlugin.() -> Unit))
        }

        /**
         * Inline pipeline assembler
         *
         * ```
         * ...
         *  assembler { target, entities ->
         *      // assembler code
         *  }
         * ...
         * ```
         * target — target entity type
         * entities — pipeline input entities
         *
         * @param block PipelineAssemblerFunction
         */
        @JvmName("assembler__function")
        @PublicApi
        fun assembler(block: PipelineAssemblerFunction<T>) {
            assembler = PipelineAssemblerWithFunction(block)
        }

        /**
         * Pipeline assembler with a plugin
         *
         * ```
         * ...
         *  assembler<AssemblerPluginClass> {
         *      // assembler options, it depends on plugin class
         *  }
         * ...
         * ```
         * * [AssemblerPluginClass][net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin] — assembler
         *      plugin class, **mandatory**
         *
         * @param block The assembler options
         */
        @JvmName("assembler__plugin")
        inline fun <reified P : PipelineAssemblerPlugin<T>> assembler(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            assembler = PipelineAssemblerWithPlugin(
                P::class as KClass<PipelineAssemblerPlugin<T>>,
                block as (PipelineAssemblerPlugin<T>.() -> Unit)
            )
        }

        /**
         * Child pipeline
         *
         * ```
         * ...
         *  pipeline(accepted classes) {
         *      // pipeline configuration
         *  }
         * ...
         * ```
         * [accepted classes][CLASS] — pipeline accepted classes
         *
         * @param klass Array<out CLASS> The pipeline accepted class
         * @param block Builder.() → Unit
         */
        @Suppress("SpreadOperator")
        fun pipeline(vararg klass: CLASS, block: Builder<T>.() -> Unit) {
            pipelines.add(Builder<T>(*klass).apply(block).build())
        }

        /**
         * Build pipeline configuration
         *
         * @return Pipeline
         */
        fun build(): Pipeline<T> {
            return Pipeline(
                dataClass,
                classifier ?: PipelineClassifierWithFunction { it.match() },
                pipelines,
                assembler
            )
        }
    }
}
