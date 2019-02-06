package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.NamedObject
import java.io.File

data class SourceFileDefinition(
    val id: String,
    val file: File
): NamedObject {
    override val name: String
        get() = id
}
