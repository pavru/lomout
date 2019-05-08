package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.EntityType

class ProductionLine(
    lineType: LineType,
    inputEntities: InputEntityCollection,
    val outputEntity: EntityType,
    pipeline: Pipeline
) : AbstractLine(lineType, inputEntities, pipeline) {
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper, private val lineType: LineType) {
        private var inputs: InputEntityCollection? = null
        private var output: EntityType? = null
        private var pipeline: Pipeline? = null

        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            inputs = InputEntityCollection.Builder(helper).apply(block).build()
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
            output = EntityType.Builder(helper, name, false).apply(block).build()
        }

        @PublicApi
        fun output(name: String) {
            output = helper.typeManager.getEntityType(name)
        }

        @Suppress("ThrowsCount")
        fun build(): ProductionLine {
            validatePipeline(
                pipeline
                    ?: throw AppConfigException("Production line must have start pipeline")
            )
            return ProductionLine(
                lineType,
                inputs ?: throw AppConfigException("At least one input entity must be defined"),
                output ?: throw AppConfigException("Output entity must be defined"),
                pipeline!!
            )
        }

        private fun validatePipeline(pipeline: Pipeline) {
            if (pipeline.pipelines.isEmpty() && pipeline.assembler == null) {
                throw AppConfigException("Pipeline with matched child must have assembler")
            }
            for (line in pipeline.pipelines) validatePipeline(line)
        }
    }
}
