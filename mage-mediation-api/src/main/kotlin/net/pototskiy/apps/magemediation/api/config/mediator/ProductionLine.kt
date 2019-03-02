package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.EType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager

data class ProductionLine(
    val lineType: LineType,
    val inputEntities: InputEntityCollection,
    val outputEntity: EType,
    val pipeline: Pipeline
) {
    enum class LineType { CROSS, UNION }
    @ConfigDsl
    class Builder(private val lineType: LineType) {
        private var inputs: InputEntityCollection? = null
        private var output: EType? = null
        private var pipeline: Pipeline? = null

        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            inputs = InputEntityCollection.Builder().apply(block).build()
        }

        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder.() -> Unit
        ) {
            pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        fun output(name: String, block: EType.Builder.() -> Unit) {
            output = EType.Builder(name, false).apply(block).build()
        }

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
