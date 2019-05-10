package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.config.NamedObject
import java.io.File
import java.util.*

/**
 * Source file definition
 *
 * @property id The unique file id
 * @property file The file
 * @property locale The file locale for converting, formatting operation
 * @property name The file name
 * @constructor
 */
data class SourceFileDefinition(
    val id: String,
    val file: File,
    val locale: Locale
) : NamedObject {
    override val name: String
        get() = id
}
