package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.Configuration
import net.pototskiy.apps.magemediation.config.DatasetTarget
import net.pototskiy.apps.magemediation.source.WorkbookFactory
import java.util.logging.Logger

object DataLoader {
    private val logger = Logger.getLogger(LOG_NAME)

    fun load() {
        val files = Configuration.config.files
        val datasets = Configuration.config.datasets
        for (dataset in datasets) {
            dataset.sources.forEach { source ->
                val file = files.findLast { it.id == source.file }
                    ?: throw LoaderException("Source file id<${source.file}> is not defined")
                val workbook = WorkbookFactory.create(file.path)
                val loader = LoaderFactory.create(mapTargetToDestination(dataset.target))
                val regex = Regex(source.sheet)
                if (!workbook.any { regex.matches(it.name) }) {
                    logger.warning("Sheet<${source.sheet}> can not be found in source<${source.file}>")
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