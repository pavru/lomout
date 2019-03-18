package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.mediator.ProductionLine
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.loader.EntityUpdater
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ProductionLineExecutor(entityTypeManager: EntityTypeManager) :
    LineExecutor(
        entityTypeManager,
        System.getProperty("mediation.line.cache.size")?.toIntOrNull() ?: 0
    ) {

    override val logger: Logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    private lateinit var entityUpdater: EntityUpdater

    override fun processResultData(data: Map<AnyTypeAttribute, Type?>): Long {
        return entityUpdater.update(data)
    }

    override fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor {
        line as ProductionLine
        return PipelineExecutor(
            entityTypeManager,
            line.pipeline,
            line.inputEntities,
            line.outputEntity,
            pipelineDataCache
        )
    }

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    override fun executeLine(line: AbstractLine): Long {
        line as ProductionLine
        val targetEntityType = line.outputEntity
        entityUpdater = EntityUpdater(targetEntityType)
        return super.executeLine(line)
    }
}
