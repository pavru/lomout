package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigurationBuilderFromDSL
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.database.initDatabase
import org.apache.logging.log4j.Level
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File

internal class LoadingDataTestPrepare {
    private lateinit var config: Config

    init {
//        BasicConfigurator.configure()
    }

    @Suppress("unused")
    internal fun loadConfiguration(): Config {
        config = ConfigurationBuilderFromDSL(
            File(this::class.java.classLoader.getResource("test.config.kts").toURI())
        ).config
        return config
    }

    internal fun loadConfiguration(file: String): Config {
        config = ConfigurationBuilderFromDSL(File(file)).config
        return config
    }

    @Suppress("unused")
    internal fun openHSSWorkbookFromResources(name: String): HSSFWorkbook {
        val testData = this::class.java.classLoader.getResourceAsStream(name)
        return HSSFWorkbook(testData)
    }

    internal fun initDataBase(entityTypeManager: EntityTypeManager) {
        initDatabase(config.database, entityTypeManager, Level.ERROR)
    }
}
