package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherFunction
import net.pototskiy.apps.magemediation.api.plugable.EntityMatcherPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import kotlin.reflect.full.createInstance

data class ProductionLine(
    val lineType: LineType,
    val inputEntities: InputEntityCollection,
    val outputEntity: Entity,
    val matcher: EntityMatcher,
    val processors: ProcessorCollection,
    // TODO: 14.02.2019 remove nullable
    val pipeline: Pipeline
) {
    enum class LineType { CROSS, UNION }
    @ConfigDsl
    class Builder(private val lineType: LineType) {
        private var inputs: InputEntityCollection? = null
        private var output: Entity? = null
        @Suppress("PropertyName")
        var __matcher: EntityMatcher? = null
        private var processors: ProcessorCollection? = null
        private var pipeline: Pipeline? = null

        @Suppress("unused")
        fun Builder.input(block: InputEntityCollection.Builder.() -> Unit) {
            inputs = InputEntityCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder.() -> Unit
        ) {
            pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        @Suppress("unused")
        fun Builder.output(name: String, block: Entity.Builder.() -> Unit) {
            output = Entity.Builder(name, false).apply(block).build()
        }

        @Suppress("unused")
        inline fun <reified P : EntityMatcherPlugin> Builder.matcher() {
            __matcher = EntityMatcherWithPlugin(P::class)
        }

        @Suppress("unused")
        @JvmName("matcher__plugin__option")
        inline fun <reified P : EntityMatcherPlugin, O : NewPlugin.Options> Builder.matcher(block: O.() -> Unit) {
            val plugin = P::class.createInstance()
            @Suppress("UNCHECKED_CAST")
            val options = (plugin.optionSetter() as O).apply(block)
            __matcher = EntityMatcherWithPlugin(P::class, options)
        }

        @Suppress("unused")
        @JvmName("matcher__function")
        fun Builder.matcher(block: EntityMatcherFunction) {
            __matcher = EntityMatcherWithFunction(block)
        }

        @Suppress("unused")
        fun Builder.processors(block: ProcessorCollection.Builder.() -> Unit) {
            processors = ProcessorCollection.Builder().apply(block).build()
        }

        fun build(): ProductionLine {
            validatePipeline(pipeline
                ?: throw ConfigException("Production line must have pipeline"))
            return ProductionLine(
                lineType,
                inputs ?: throw ConfigException("At least one input entity must be defined"),
                output ?: throw ConfigException("Output entity must be defined"),
                __matcher ?: throw ConfigException("Entity matcher must be defined"),
                processors ?: ProcessorCollection(emptyList()),
                pipeline!!
            )
        }

        private fun validatePipeline(pipeline: Pipeline) {
            if (pipeline.pipelines.isEmpty() && pipeline.assembler == null) {
                throw ConfigException("Pipeline with matched child must have assembler")
            }
            for (line in pipeline.pipelines) validatePipeline(line)
        }
    }
}
