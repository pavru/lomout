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
import net.pototskiy.apps.lomout.api.callable.PipelineAssembler
import net.pototskiy.apps.lomout.api.callable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.callable.PipelineClassifierFunction
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.script.pipeline.Classifier
import net.pototskiy.apps.lomout.api.script.pipeline.ClassifierWithFunction
import net.pototskiy.apps.lomout.api.script.pipeline.ClassifierWithPlugin
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
    val classifier: Classifier,
    val pipelines: List<Pipeline<T>>,
    val assembler: Assembler<T>?
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
        var classifier: Classifier? = null
        /**
         * Pipeline assembler, **do not use in DSL**
         */
        var assembler: Assembler<T>? = null
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
            classifier = ClassifierWithFunction(block)
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
         * [ClassifierPluginClass][net.pototskiy.apps.lomout.api.callable.PipelineClassifier] — classifier
         *      plugin class
         *
         * @param block The classifier options
         */
        @JvmName("classifier__plugin")
        inline fun <reified P : net.pototskiy.apps.lomout.api.callable.PipelineClassifier> classifier(
            noinline block: P.() -> Unit = {}
        ) {
            @Suppress("UNCHECKED_CAST")
            classifier = ClassifierWithPlugin(
                P::class,
                block as (net.pototskiy.apps.lomout.api.callable.PipelineClassifier.() -> Unit)
            )
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
            assembler = AssemblerWithFunction(block)
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
         * * [AssemblerPluginClass][net.pototskiy.apps.lomout.api.callable.PipelineAssembler] — assembler
         *      plugin class, **mandatory**
         *
         * @param block The assembler options
         */
        @JvmName("assembler__plugin")
        inline fun <reified P : PipelineAssembler<T>> assembler(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            assembler = AssemblerWithCallable(
                P::class as KClass<PipelineAssembler<T>>,
                block as (PipelineAssembler<T>.() -> Unit)
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
                classifier ?: ClassifierWithFunction { it.match() },
                pipelines,
                assembler
            )
        }
    }
}
