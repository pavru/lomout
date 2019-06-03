package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.mediator.InputEntityCollection
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.unknownPlace

/**
 * Printer line
 *
 * @property outputFieldSets PrinterOutput
 * @constructor
 * @param inputEntities InputEntityCollection The input entities collection
 * @param outputFieldSets PrinterOutput The printer line output
 * @param pipeline Pipeline The printer line pipeline
 */
class PrinterLine(
    inputEntities: InputEntityCollection,
    val outputFieldSets: PrinterOutput,
    pipeline: Pipeline
) : AbstractLine(inputEntities, pipeline) {
    /**
     * Printer line builder class
     *
     * @property helper ConfigBuildHelper The configuration builder helper
     * @property inputs InputEntityCollection? The input entities collection
     * @property pipeline Pipeline? The printer line pipeline
     * @property outputs PrinterOutput? The printer line output
     * @constructor
     * @param helper ConfigBuilderHelper The config builder helper
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var inputs: InputEntityCollection? = null
        private var pipeline: Pipeline? = null
        private var outputs: PrinterOutput? = null

        /**
         * Define inputs for printer line
         *
         * ```
         * ...
         *  input {
         *      entity("entity_type") {
         *          filter {...}
         *          filter<FilterPluginClass> {}
         *      }
         *      entity("entity_type") {
         *          filter {...}
         *      }
         *  }
         * ...
         * ```
         * * entity — define entity for line input, **at least one entity must be defined**
         * * entity_type — entity type name
         *
         * Printer line generate stream of entity from all defined inputs like SQL UNION
         *
         * @param block The pipeline input definition
         */
        @ConfigDsl
        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            this.inputs = InputEntityCollection.Builder(helper).also(block).build()
            this.inputs?.let {
                if (it.size != 1) {
                    throw AppConfigException(
                        unknownPlace(),
                        "One and only one input entity is allowed for printer line."
                    )
                }
                if (it.first().extAttrMaps.isNotEmpty()) {
                    throw AppConfigException(
                        unknownPlace(),
                        "Input entity of printer line cannot have extended attributes."
                    )
                }
            }
        }

        /**
         * Define print line output
         *
         * ```
         * ...
         *  output {
         *      file {...}
         *      printHead = true
         *      outputFields {
         *          main("set name") {
         *              field("field name")
         *              ...
         *          }
         *          extra("set_name") {
         *              field("field name")
         *              ...
         *          }
         *      }
         *  }
         * ...
         * ```
         * * [file][net.pototskiy.apps.lomout.api.config.loader.SourceData.Builder] —
         *      define output file, **mandatory**
         * * printHead: Boolean — print headers in first row, *optional, default: true*
         * * [outputFields][net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection.Builder] —
         *      define fields to print, **mandatory**
         *
         * @param block The output definition
         */
        @ConfigDsl
        fun output(block: PrinterOutput.Builder.() -> Unit) {
            this.outputs = PrinterOutput.Builder(
                helper,
                inputs?.first()?.entity
                    ?: throw AppConfigException(unknownPlace(), "Input must be defined before output.")
            ).apply(block).build()
        }

        /**
         * Define printer pipeline to process entities, this is root element of the pipeline tree
         *
         * ```
         * ...
         *  pipeline {
         *      classifier {...}
         *      classifier<ClassifierPluginClass>()
         *      pipeline(Pipeline.CLASS.MATCHED) {
         *          assembler { target, entities ->
         *              // assembler code
         *          }
         *          assembler<AssemblerPluginClass> {
         *              // assembler options, it depends on assembler class
         *          }
         *      }
         *      pipeline(Pipeline.CLASS.UNMATCHED) {
         *          classifier {...}
         *          pipeline(Pipeline.CLASS.MATCHED) {
         *              classifier {...}
         *              pipeline(Pipeline.CLASS.MATCHED) {
         *                  assembler {...}
         *              }
         *              pipeline(Pipeline.CLASS.UNMATCHED) {
         *                  assembler {...}
         *              }
         *          }
         *          pipeline(Pipeline.CLASS.UNMATCHED) {
         *              assembler {...}
         *          }
         *      }
         *  }
         * ...
         * ```
         * * [classifier][Pipeline.Builder.classifier] — entity classifier, it splits input of pipeline into 2 streams:
         *      matched entities and unmatched entities. This is *optional* component, **only one classifier is allowed
         *      per pipeline**. If classifier is omitted all entities go to assembler.
         * * [assembler][Pipeline.Builder.assembler] — entity assembler, it prepares target entity attribute as map.
         *      If pipeline has not child pipeline it must have assembler.
         * * [pipeline][Pipeline.Builder.pipeline] — child pipeline, parameter indicates for which entities (matched,
         *      unmatched) pipeline must be applied
         *
         * @param klass Array<out CLASS>
         * @param block The pipeline definition
         */
        @ConfigDsl
        @Suppress("SpreadOperator")
        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder.() -> Unit
        ) {
            this.pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        /**
         * Build printer line
         *
         * @return PrinterLine
         */
        @Suppress("ThrowsCount")
        fun build(): PrinterLine {
            validatePipeline(pipeline ?: throw AppConfigException(unknownPlace(), "Pipeline must be defined."))
            return PrinterLine(
                inputs ?: throw AppConfigException(unknownPlace(), "Input entities must be defined."),
                outputs ?: throw AppConfigException(unknownPlace(), "Output fields must be defined."),
                pipeline ?: throw AppConfigException(unknownPlace(), "Pipeline must be defined.")
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
