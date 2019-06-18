package net.pototskiy.apps.lomout.mediator

import net.pototskiy.apps.lomout.api.MEDIATOR_LOG_NAME
import net.pototskiy.apps.lomout.api.config.mediator.AbstractLine
import net.pototskiy.apps.lomout.api.config.mediator.ProductionLine
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.loader.EntityUpdater
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ProductionLineExecutor(repository: EntityRepositoryInterface) : LineExecutor(repository) {

    override val logger: Logger = LogManager.getLogger(MEDIATOR_LOG_NAME)
    private lateinit var entityUpdater: EntityUpdater

    override fun processResultData(data: Map<AnyTypeAttribute, Type>): Long =
        if (data.isEmpty()) {
            0L
        } else {
            entityUpdater.update(data)
        }

    override fun preparePipelineExecutor(line: AbstractLine): PipelineExecutor {
        line as ProductionLine
        return PipelineExecutor(
            repository.entityTypeManager,
            line.pipeline,
            line.inputEntities,
            line.outputEntity
        )
    }

    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    override fun executeLine(line: AbstractLine): Long {
        line as ProductionLine
        val targetEntityType = line.outputEntity
        entityUpdater = EntityUpdater(repository, targetEntityType)
        return super.executeLine(line)
    }
}
