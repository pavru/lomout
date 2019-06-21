package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.unknownPlace

/**
 * Mediator production line configuration
 *
 * @property outputEntity EntityType The output entity type
 * @constructor
 * @param inputEntities InputEntityCollection The input configuration
 * @param outputEntity EntityType The output entity type
 * @param pipeline Pipeline The root pipeline
 */
class ProductionLine(
    inputEntities: InputEntityCollection,
    val outputEntity: EntityType,
    pipeline: Pipeline
) : AbstractLine(inputEntities, pipeline) {
    /**
     * Production line configuration builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property inputs InputEntityCollection? The input entities definition
     * @property output EntityType? The output entity type
     * @property pipeline Pipeline? The root pipeline
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var inputs: InputEntityCollection? = null
        private var output: EntityType? = null
        private var pipeline: Pipeline? = null

        /**
         * Production line input definition
         *
         * ```
         * ...
         *  input {
         *      entity("entity type name") {
         *          filter {
         *          }
         *          extAttribute<Type>("new attr","source attr") {...}
         *          extAttribute<Type>("new attr","source attr") {...}
         *          ...
         *      }
         *      entity("entity type name") {...}
         *      entity("entity type name") {...}
         *      ...
         *  }
         * ...
         * ```
         * * [entity][InputEntityCollection.Builder.entity] — define input entity, **at least one must be defined**
         *
         * @param block The pipeline input definition
         */
        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            inputs = InputEntityCollection.Builder(helper).apply(block).build()
        }

        /**
         * Define root pipeline of line
         *
         * ```
         * ...
         *  pipeline {
         *      classifier {...}
         *      pipeline {...}
         *      assembler {...}
         *  }
         * ...
         * ```
         *
         * @param klass Array<out CLASS>
         * @param block The pipeline definition
         */
        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder.() -> Unit
        ) {
            @Suppress("SpreadOperator")
            pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        /**
         * Define production line output entity type
         *
         * ```
         * ...
         *  output("entity type name") {
         *      copyFrom("name") {...}
         *      attribute<Type> {...}
         *      attribute<Type> {...}
         *      ...
         *  }
         * ...
         * ```
         *
         * @param name String The entity type name
         * @param block The pipeline output definition
         */
        @PublicApi
        fun output(name: String, block: EntityType.Builder.() -> Unit) {
            output = EntityType.Builder(helper, name, false).apply(block).build()
        }

        /**
         * Define production line output as reference to existing entity type
         *
         * ```
         * ...
         *  output("existing entity type name")
         * ...
         * ```
         *
         * @param name String The already defined entity type name
         */
        @PublicApi
        fun output(name: String) {
            output = helper.typeManager.getEntityType(name)
        }

        /**
         * Build production line configuration
         *
         * @return ProductionLine
         */
        @Suppress("ThrowsCount")
        fun build(): ProductionLine {
            validatePipeline(
                pipeline
                    ?: throw AppConfigException(unknownPlace(), "Production line must have start pipeline.")
            )
            return ProductionLine(
                inputs ?: throw AppConfigException(unknownPlace(), "At least one input entity must be defined."),
                output ?: throw AppConfigException(unknownPlace(), "Output entity must be defined."),
                pipeline!!
            )
        }

        private fun validatePipeline(pipeline: Pipeline) {
            if (pipeline.pipelines.isEmpty() && pipeline.assembler == null) {
                throw AppConfigException(unknownPlace(), "Pipeline with the matched child must have assembler.")
            }
            for (line in pipeline.pipelines) validatePipeline(line)
        }
    }
}
