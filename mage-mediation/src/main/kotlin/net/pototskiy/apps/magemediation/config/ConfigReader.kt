package net.pototskiy.apps.magemediation.config

import net.pototskiy.apps.magemediation.Args
import net.pototskiy.apps.magemediation.LOG_NAME
import org.apache.log4j.Logger
import java.io.File
import javax.xml.bind.JAXBContext

fun readConfig(): Config? {
    val logger = Logger.getLogger(LOG_NAME)
    val jaxbContext = JAXBContext.newInstance(Config::class.java)
    val unmarshaller = jaxbContext.createUnmarshaller()
    unmarshaller.listener = UnmarshallerListener()
    var config: Config? = null
    try {
        File(Args.configFile).reader().use {
            config = unmarshaller.unmarshal(it) as Config
        }
    } catch (e: Exception) {
        logger.error("Configuration error: ${e.message}")
        if ( e !is ConfigException) {
            logger.error("Internal error", e)
        }
        System.exit(1)
    }
    return config
}
