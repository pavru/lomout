package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class ProductionLine(
    val lineType: LineType,
    val inputEntities: InputEntityCollection,
    val outputEntity: EntityType,
    val pipeline: Pipeline
) {
    enum class LineType { CROSS, UNION }
    @ConfigDsl
    class Builder(private val lineType: LineType) {
        private var inputs: InputEntityCollection? = null
        private var output: EntityType? = null
        private var pipeline: Pipeline? = null

        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            inputs = InputEntityCollection.Builder().apply(block).build()
        }

        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder.() -> Unit
        ) {
            @Suppress("SpreadOperator")
            pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        @PublicApi
        fun output(name: String, block: EntityType.Builder.() -> Unit) {
            output = EntityType.Builder(name, false).apply(block).build()
        }

        @PublicApi
        fun output(name: String) {
            output = EntityTypeManager.getEntityType(name)
        }

        @Suppress("ThrowsCount")
        fun build(): ProductionLine {
            validatePipeline(pipeline
                ?: throw ConfigException("Production line must have plugins.pipeline"))
            return ProductionLine(
                lineType,
                inputs ?: throw ConfigException("At least one input entity must be defined"),
                output ?: throw ConfigException("Output entity must be defined"),
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
