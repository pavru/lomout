package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.pipeline.PipelineClassifier
import net.pototskiy.apps.lomout.api.config.pipeline.PipelineClassifierWithFunction
import net.pototskiy.apps.lomout.api.config.pipeline.PipelineClassifierWithPlugin
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierPlugin
import java.util.*

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
data class Pipeline(
    val dataClass: List<CLASS>,
    val classifier: PipelineClassifier,
    val pipelines: List<Pipeline>,
    val assembler: PipelineAssembler?
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
    @ConfigDsl
    class Builder(vararg klass: CLASS) {
        private val dataClass = klass.toList()
        /**
         * Pipeline classifier, **do not use in DSL**
         */
        var classifier: PipelineClassifier? = null
        /**
         * Pipeline assembler, **do not use in DSL**
         */
        var assembler: PipelineAssembler? = null
        private var pipelines = mutableListOf<Pipeline>()

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
        fun assembler(block: PipelineAssemblerFunction) {
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
        inline fun <reified P : PipelineAssemblerPlugin> assembler(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            assembler = PipelineAssemblerWithPlugin(P::class, block as (PipelineAssemblerPlugin.() -> Unit))
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
        fun pipeline(vararg klass: CLASS, block: Builder.() -> Unit) {
            pipelines.add(Builder(*klass).apply(block).build())
        }

        /**
         * Build pipeline configuration
         *
         * @return Pipeline
         */
        fun build(): Pipeline {
            return Pipeline(
                dataClass,
                classifier ?: PipelineClassifierWithFunction { it.match() },
                pipelines,
                assembler
            )
        }
    }
}
