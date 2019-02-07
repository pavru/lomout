package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigurationBuilderFromDSL
import net.pototskiy.apps.magemediation.database.initDatabase
import org.apache.logging.log4j.Level
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File

class LoadingDataTestPrepare {
    private lateinit var config: Config

    init {
//        BasicConfigurator.configure()
    }

    @Suppress("unused")
    fun loadConfiguration(): Config {
        config = ConfigurationBuilderFromDSL(
            File(this::class.java.classLoader.getResource("test.config.kts").toURI())
        ).config
        return config
    }

    fun loadConfiguration(file: String): Config {
        config = ConfigurationBuilderFromDSL(File(file)).config
        return config
    }

    @Suppress("unused")
    fun openHSSWorkbookFromResources(name: String): HSSFWorkbook {
        val testData = this::class.java.classLoader.getResourceAsStream(name)
        return HSSFWorkbook(testData)
    }

    fun initDataBase() {
        initDatabase(config.database, Level.ERROR)
    }
}
