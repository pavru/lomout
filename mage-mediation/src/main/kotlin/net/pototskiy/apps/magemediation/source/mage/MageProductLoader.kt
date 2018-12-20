package net.pototskiy.apps.magemediation.source.mage

import net.pototskiy.apps.magemediation.config.Configuration
import net.pototskiy.apps.magemediation.loader.LoadDestination
import net.pototskiy.apps.magemediation.loader.LoaderException
import net.pototskiy.apps.magemediation.loader.LoaderFactory
import net.pototskiy.apps.magemediation.source.WorkbookFactory
import org.slf4j.LoggerFactory

class MageProductLoader(private val file: String) {
    private val logger = LoggerFactory.getLogger("import")

    fun load() {
        val wb = WorkbookFactory.create(file)
        val sheet = wb[0]
        val loader = LoaderFactory.create(LoadDestination.MAGE_PRODUCT)
        val dataset = Configuration.config.datasets.findLast { it.name == "mage_product" }
            ?: throw LoaderException("Can not find mage_product dataset")
        loader.load(sheet, dataset)
    }

}