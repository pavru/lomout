package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.magemediation.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierFunction
import net.pototskiy.apps.magemediation.api.plugable.PipelineClassifierPlugin
import java.util.*

data class Pipeline(
    val dataClass: List<CLASS>,
    val classifier: PipelineClassifier,
    val pipelines: List<Pipeline>,
    val assembler: PipelineAssembler?
) {
    val pipelineID = UUID.randomUUID().toString()

    enum class CLASS { MATCHED, UNMATCHED }

    @PublicApi
    fun isApplicablePipeline(vararg klass: CLASS): Boolean = dataClass.containsAll(klass.toList())

    class Builder(vararg klass: CLASS) {
        private val dataClass = klass.toList()
        @ConfigDsl
        var classifier: PipelineClassifier? = null
        @ConfigDsl
        var assembler: PipelineAssembler? = null
        private var pipelines = mutableListOf<Pipeline>()

        @JvmName("classifier__function")
        @PublicApi
        fun classifier(block: PipelineClassifierFunction) {
            classifier = PipelineClassifierWithFunction(block)
        }

        @JvmName("classifier__plugin")
        inline fun <reified P : PipelineClassifierPlugin> classifier(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            classifier = PipelineClassifierWithPlugin(P::class, block as (PipelineClassifierPlugin.() -> Unit))
        }

        @JvmName("assembler__function")
        @PublicApi
        fun assembler(block: PipelineAssemblerFunction) {
            assembler = PipelineAssemblerWithFunction(block)
        }

        @JvmName("assembler__plugin")
        inline fun <reified P : PipelineAssemblerPlugin> assembler(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            assembler = PipelineAssemblerWithPlugin(P::class, block as (PipelineAssemblerPlugin.() -> Unit))
        }

        @Suppress("SpreadOperator")
        fun pipeline(vararg klass: CLASS, block: Builder.() -> Unit) {
            pipelines.add(Builder(*klass).apply(block).build())
        }

        fun build(): Pipeline {
            return Pipeline(
                dataClass,
                classifier ?: PipelineClassifierWithFunction { CLASS.MATCHED },
                pipelines,
                assembler
            )
        }
    }
}
