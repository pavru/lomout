package net.pototskiy.apps.magemediation.api.config.printer

import net.pototskiy.apps.magemediation.api.AppConfigException
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.mediator.AbstractLine
import net.pototskiy.apps.magemediation.api.config.mediator.InputEntityCollection
import net.pototskiy.apps.magemediation.api.config.mediator.Pipeline

class PrinterLine(
    inputEntities: InputEntityCollection,
    val outputFieldSets: PrinterOutput,
    pipeline: Pipeline
) : AbstractLine(LineType.UNION, inputEntities, pipeline) {
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var inputs: InputEntityCollection? = null
        private var pipeline: Pipeline? = null
        private var outputs: PrinterOutput? = null

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

        @ConfigDsl
        fun output(block: PrinterOutput.Builder.() -> Unit) {
            this.outputs = PrinterOutput.Builder(
                helper,
                inputs?.first()?.entity
                    ?: throw AppConfigException("Input must be defined before output")
            ).apply(block).build()
        }

        @ConfigDsl
        @Suppress("SpreadOperator")
        fun pipeline(
            vararg klass: Pipeline.CLASS = arrayOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED),
            block: Pipeline.Builder.() -> Unit
        ) {
            this.pipeline = Pipeline.Builder(*klass).apply(block).build()
        }

        @Suppress("ThrowsCount")
        fun build(): PrinterLine {
            return PrinterLine(
                inputs ?: throw AppConfigException("Input entities must be defined"),
                outputs ?: throw AppConfigException("Output fields must be defined"),
                pipeline ?: throw AppConfigException("Pipeline must be defined")
            )
        }
    }
}
