package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.Configuration
import net.pototskiy.apps.magemediation.database.initDatabase
import org.apache.logging.log4j.Level
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File

class LoadingDataTestPrepare {
    private lateinit var config: Config

    init {
//        BasicConfigurator.configure()
    }

    fun loadConfiguration(): Config {
        config = Configuration(
            this::class.java.classLoader.getResourceAsStream("test.config.kts")
        ).config
        return config
    }

    fun loadConfiguration(file: String): Config {
        config = Configuration(
            File(file).toURI().toURL().openStream()
        ).config
        return config
    }

    fun openHSSWorkbookFromResources(name: String): HSSFWorkbook {
        val testData = this::class.java.classLoader.getResourceAsStream(name)
        return HSSFWorkbook(testData)
    }

    fun initDataBase() {
        initDatabase(config.database, Level.ERROR)
    }
}