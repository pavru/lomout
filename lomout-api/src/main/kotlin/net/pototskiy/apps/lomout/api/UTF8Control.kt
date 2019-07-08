package net.pototskiy.apps.lomout.api

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Resource bundle UTF-8 support
 *
 */
class UTF8Control : ResourceBundle.Control() {
    /**
     * Create a new bundle
     *
     * @param baseName String
     * @param locale Locale
     * @param format String
     * @param loader ClassLoader
     * @param reload Boolean
     * @return ResourceBundle?
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    @Throws(IllegalAccessException::class, InstantiationException::class, IOException::class)
    override fun newBundle(
        baseName: String,
        locale: Locale,
        format: String,
        loader: ClassLoader,
        reload: Boolean
    ): ResourceBundle? {
        // The below is a copy of the default implementation.
        val bundleName = toBundleName(baseName, locale)
        val resourceName = toResourceName(bundleName, "properties")
        var bundle: ResourceBundle? = null
        var stream: InputStream? = null
        if (reload) {
            val url = loader.getResource(resourceName)
            if (url != null) {
                val connection = url.openConnection()
                if (connection != null) {
                    connection.useCaches = false
                    stream = connection.getInputStream()
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName)
        }
        if (stream != null) {
            try {
                // Only this line is changed to make it to read properties files as UTF-8.
                bundle = PropertyResourceBundle(InputStreamReader(stream, "UTF-8"))
            } finally {
                stream.close()
            }
        }
        return bundle
    }
}
