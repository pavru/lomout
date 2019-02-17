package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.plugable.*
import java.util.*
import kotlin.reflect.full.createInstance


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
        @Suppress("PropertyName")
        var __classifier: PipelineClassifier? = null
        @Suppress("PropertyName")
        var __assembler: PipelineAssembler? = null
        private var pipelines = mutableListOf<Pipeline>()

        @Suppress("unused")
        @PublicApi
        @JvmName("classifier__function")
        fun Builder.classifier(block: PipelineClassifierFunction) {
            __classifier = PipelineClassifierWithFunction(block)
        }

        @Suppress("unused")
        @PublicApi
        @JvmName("classifier__plugin")
        inline fun <reified P : PipelineClassifierPlugin> Builder.classifier() {
            __classifier = PipelineClassifierWithPlugin(P::class)
        }

        @Suppress("unused")
        @PublicApi
        @JvmName("classifier__plugin__options")
        inline fun <reified P : PipelineClassifierPlugin, O : NewPlugin.Options> Builder.classifier(block: O.() -> Unit) {
            @Suppress("UNCHECKED_CAST")
            val options = (P::class.createInstance().optionSetter() as O).apply(block)
            __classifier = PipelineClassifierWithPlugin(P::class, options)
        }

        @Suppress("unused")
        @PublicApi
        @JvmName("assembler__function")
        fun Builder.assembler(block: PipelineAssemblerFunction) {
            __assembler = PipelineAssemblerWithFunction(block)
        }

        @PublicApi
        @Suppress("unused")
        @JvmName("assembler__plugin")
        inline fun <reified P : PipelineAssemblerPlugin> Builder.assembler() {
            __assembler = PipelineAssemblerWithPlugin(P::class)
        }

        @PublicApi
        @Suppress("unused")
        @JvmName("assembler__plugin__options")
        inline fun <reified P : PipelineAssemblerPlugin, O : NewPlugin.Options> Builder.assembler(block: O.() -> Unit) {
            @Suppress("UNCHECKED_CAST")
            val options = (P::class.createInstance().optionSetter() as O).apply(block)
            __assembler = PipelineAssemblerWithPlugin(P::class, options)
        }

        @PublicApi
        @Suppress("unused")
        fun Builder.pipeline(vararg klass: CLASS, block: Builder.() -> Unit) {
            pipelines.add(Builder(*klass).apply(block).build())
        }

        fun build(): Pipeline {
            return Pipeline(
                dataClass,
                __classifier ?: PipelineClassifierWithFunction { CLASS.MATCHED },
                pipelines,
                __assembler
            )
        }
    }

}
