package net.pototskiy.apps.lomout.api.config

import java.io.File
import java.io.Serializable
import java.net.URL
import kotlin.script.experimental.api.ExternalSourceCode

/**
 * Script source
 *
 * @property file The script file
 * @property preloadedText The script preloaded text
 * @property externalLocation The script location
 * @property text The script text
 * @property name The script file name
 * @property locationId The script location (path)
 * @property textSafe The script text
 * @constructor
 */
class SerializableFileScriptSource(
    val file: File,
    private val preloadedText: String? = null
) : ExternalSourceCode, Serializable {
    override val externalLocation: URL get() = file.toURI().toURL()
    override val text: String by lazy { preloadedText ?: file.readText() }
    override val name: String? get() = file.name
    override val locationId: String? get() = file.path

    /**
     * Is equal
     *
     * @param other
     * @return
     */
    override fun equals(other: Any?): Boolean =
        this === other ||
                (other as? SerializableFileScriptSource)?.let {
                    file.absolutePath == it.file.absolutePath && textSafe == it.textSafe
                } == true

    /**
     * Object hash code
     *
     * @return Int
     */
    override fun hashCode(): Int = file.absolutePath.hashCode() + textSafe.hashCode() * 23

    @Suppress("TooGenericExceptionCaught")
    private val ExternalSourceCode.textSafe: String?
        get() =
            try {
                text
            } catch (e: Throwable) {
                null
            }
}
