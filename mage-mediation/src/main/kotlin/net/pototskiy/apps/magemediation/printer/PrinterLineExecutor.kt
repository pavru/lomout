package net.pototskiy.apps.magemediation.printer

import net.pototskiy.apps.magemediation.api.PRINTER_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.mediator.AbstractLine
import net.pototskiy.apps.magemediation.api.config.printer.PrinterLine
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.mediator.LineExecutor
import net.pototskiy.apps.magemediation.mediator.PipelineExecutor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class PrinterLineExecutor(entityTypeManager: EntityTypeManager) :
    LineExecutor(
        entityTypeManager,
        System.getenv("printer.line.cache.size")?.toIntOrNull() ?: 0
    ) {

    override val logger: Logger = LogManager.getLogger(PRINTER_LOG_NAME)
    private lateinit var printer: EntityPrinter
    override fun processResultData(data: Map<AnyTypeAttribute, Type?>): Long {
        return printer.print(data)
    }

    override fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor = PipelineExecutor(
        entityTypeManager,
        line.pipeline,
        line.inputEntities,
        line.inputEntities.first().entity,
        pipelineDataCache
    )

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    override fun executeLine(line: AbstractLine): Long {
        line as PrinterLine
        val entityPrinter = EntityPrinter(
            line.outputFieldSets.file,
            line.outputFieldSets.fieldSets,
            line.outputFieldSets.printHead
        )
        entityPrinter.use {
            this.printer = it
            super.executeLine(line)
        }
        return processedRows
    }
}
