package net.pototskiy.apps.magemediation.config

import net.pototskiy.apps.magemediation.LOG_NAME
import org.apache.commons.io.input.TeeInputStream
import org.apache.log4j.Logger
import java.io.File
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import javax.script.ScriptEngineManager
import javax.xml.bind.JAXBContext
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class Configuration(private val input: InputStream) {

    constructor(file: String) : this(File(file).inputStream())

    private val inForConfigRead: InputStream
    private val inForConfigValidate: InputStream
    init {
        inForConfigRead = PipedInputStream()
        inForConfigValidate = TeeInputStream(input, PipedOutputStream(inForConfigRead))
    }

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
        validateAgainstSchema()
        val logger = Logger.getLogger(LOG_NAME)
        val jaxbContext = JAXBContext.newInstance(Config::class.java)
        val unmarshaller = jaxbContext.createUnmarshaller()
        unmarshaller.listener = UnmarshallerListener()
        var config: Config? = null
        try {
            inForConfigRead.use {
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

    private fun validateAgainstSchema() {
        val v = ScriptEngineManager(Thread.currentThread().contextClassLoader).getEngineByExtension("kts")
        inForConfigValidate.use {
            val doc = StreamSource(it.reader())
            val schemaDoc = StreamSource(File("E:\\home\\alexander\\Development\\Web\\oooast-tools\\mage-mediation-api\\src\\main\\xml\\config\\schema\\config.xsd"))
            val sf = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1")
            sf.setProperty("http://saxon.sf.net/feature/xsd-version", "1.1")
            val s = sf.newSchema(schemaDoc)
            val v = s.newValidator()
            v.validate(doc)
        }
    }

}
