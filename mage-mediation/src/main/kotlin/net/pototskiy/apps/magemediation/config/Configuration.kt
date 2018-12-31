package net.pototskiy.apps.magemediation.config

import net.pototskiy.apps.magemediation.LOG_NAME
import org.apache.log4j.Logger
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import javax.xml.bind.JAXBContext

class Configuration(private val input: InputStreamReader) {

    constructor(file: String) : this(File(file).reader())
    constructor(stream: InputStream) : this(stream.reader())

    private var configCache: Config? = null
    private val logger = Logger.getLogger("import")
    val config: Config
        get() {
            val v = configCache
            return if (v != null) {
                v
            } else {
                configCache = readConfig()
                if (!validateConfig(configCache)) {
                    System.exit(1)
                }
                configCache as Config
            }
        }

    private fun validateConfig(config: Config?): Boolean {
        if (config == null) {
            logger.error("Configuration is not loaded")
            return false
        }
        return true
    }

    private fun readConfig(): Config? {
        val logger = Logger.getLogger(LOG_NAME)
        val jaxbContext = JAXBContext.newInstance(Config::class.java)
        val unmarshaller = jaxbContext.createUnmarshaller()
        unmarshaller.listener = UnmarshallerListener()
        var config: Config? = null
        try {
            input.use {
                config = unmarshaller.unmarshal(it) as Config
            }
        } catch (e: Exception) {
            logger.error("Configuration error: ${e.message}")
            if (e !is ConfigException) {
                logger.error("Internal error", e)
            }
            System.exit(1)
        }
        return config
    }

}
