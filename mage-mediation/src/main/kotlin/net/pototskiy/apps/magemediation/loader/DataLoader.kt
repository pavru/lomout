package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.DatasetTarget
import net.pototskiy.apps.magemediation.config.Config
import net.pototskiy.apps.magemediation.source.WorkbookFactory
import java.io.File
import java.util.logging.Logger

object DataLoader {
    private val logger = Logger.getLogger(LOG_NAME)

    fun load(config: Config) {
        val files = config.loader.files
        val datasets = config.loader.datasets
        for (dataset in datasets) {
            dataset.sources.forEach { source ->
                val file = files.findLast { it.id == source.fileId }
                    ?: throw LoaderException("Source file<${source.fileId}> is not configured")
                val workbook = WorkbookFactory.create(File(file.path).toURI().toURL())
                val loader = LoaderFactory.create(mapTargetToDestination(dataset.target))
                val regex = Regex(source.sheet)
                if (!workbook.any { regex.matches(it.name) }) {
                    logger.warning("Sheet<${source.sheet}> can not be found in source<${source.fileId}>")
                } else {
                    workbook.forEach {
                        if (regex.matches(it.name)) {
                            loader.load(it, dataset, source.emptyRowAction)
                        }
                    }
                }

            }
        }
    }

    private fun mapTargetToDestination(target: DatasetTarget): LoadDestination = when (target) {
        DatasetTarget.ONEC_PRODUCT -> LoadDestination.ONEC_PRODUCT
        DatasetTarget.ONEC_GROUP -> LoadDestination.ONEC_CATEGORY
        DatasetTarget.MAGE_PRODUCT -> LoadDestination.MAGE_PRODUCT
        DatasetTarget.MAGE_CATEGORY -> LoadDestination.MAGE_CATEGORY
        DatasetTarget.MAGE_PRICE -> LoadDestination.MAGE_PRICING
        DatasetTarget.MAGE_INVENTORY -> LoadDestination.MAGE_INVENTORY
        DatasetTarget.MAGE_USER_GROUP -> LoadDestination.MAGE_USER_GROUP
    }
}