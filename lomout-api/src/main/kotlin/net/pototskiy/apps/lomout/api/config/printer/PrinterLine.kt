package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.mediator.InputEntityCollection
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline

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
) : AbstractLine(LineType.UNION, inputEntities, pipeline) {
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
         * * entity - define entity for line input, **at least one entity must be defined**
         * * entity_type - entity type name
         *
         * Printer line generate stream of entity from all defined inputs like SQL UNION
         *
         * @param block InputEntityCollection.Builder.() -> Unit
         */
        @ConfigDsl
        fun input(block: InputEntityCollection.Builder.() -> Unit) {
            this.inputs = InputEntityCollection.Builder(helper).also(block).build()
            this.inputs?.let {
                if (it.size != 1) {
                    throw AppConfigException("One and only one input entity is allowed for printer line")
                }
                if (it.first().extAttrMaps.isNotEmpty()) {
                    throw AppConfigException("Input entity of printer line can not have extended attributes")
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
         * [file][net.pototskiy.apps.lomout.api.config.loader.SourceData.Builder] - define output file, **mandatory**
         * printHead: Boolean - print headers in first row, *optional, default: true*
         * [outputFields][net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection.Builder] - define fields to print, **mandatory**
         *
         * @param block PrinterOutput.Builder.() -> Unit
         */
        @ConfigDsl
        fun output(block: PrinterOutput.Builder.() -> Unit) {
            this.outputs = PrinterOutput.Builder(
                helper,
                inputs?.first()?.entity
                    ?: throw AppConfigException("Input must be defined before output")
            ).apply(block).build()
        }

        /**
         * Define printer pipeline to process entities, this is root element of pipelines tree
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
         * * [classifier][Pipeline.Builder.classifier] - entity classifier, it splits input of pipeline into 2 streams:
         *      matched entities and unmatched entities. This is *optional* component, **only one classifier is allowed
         *      per pipeline**. If classifier is omitted all entities go to assembler.
         * * [assembler][Pipeline.Builder.assembler] - entity assembler, it prepares target entity attribute as map.
         *      If pipeline has not child pipeline it must have assembler.
         * * [pipeline][Pipeline.Builder.pipeline] - child pipeline, it's parameter indicate for which entities (matched,
         *      unmatched) pipeline must be applied
         *
         * @param klass Array<out CLASS>
         * @param block Pipeline.Builder.() -> Unit
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
            validatePipeline(pipeline ?: throw AppConfigException("Pipeline must be defined"))
            return PrinterLine(
                inputs ?: throw AppConfigException("Input entities must be defined"),
                outputs ?: throw AppConfigException("Output fields must be defined"),
                pipeline ?: throw AppConfigException("Pipeline must be defined")
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
