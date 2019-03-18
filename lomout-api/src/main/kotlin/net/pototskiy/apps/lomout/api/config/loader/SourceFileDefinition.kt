package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.config.NamedObject
import java.io.File
import java.util.*

data class SourceFileDefinition(
    val id: String,
    val file: File,
    val locale: Locale
) : NamedObject {
    override val name: String
        get() = id
}
