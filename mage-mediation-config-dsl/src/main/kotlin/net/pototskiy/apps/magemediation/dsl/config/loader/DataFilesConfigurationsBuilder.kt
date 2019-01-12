package net.pototskiy.apps.magemediation.dsl.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.loader.DataFileConfiguration

@ConfigDsl
class DataFilesConfigurationsBuilder {
    private val files = mutableListOf<DataFileConfiguration>()

    infix fun String.isIdOf(path: String) {
        files.add(DataFileConfiguration(this, path))
    }

    @Suppress("unused")
    fun DataFilesConfigurationsBuilder.id(id: String) =
        FileID(id)

    infix fun FilePath.linkedToID(id: String) = files.add(
        DataFileConfiguration(
            id,
            this.path
        )
    )
    @Suppress("unused")
    fun DataFilesConfigurationsBuilder.path(path: String) =
        FilePath(path)

    infix fun FileID.assignedToPath(path: String) = files.add(
        DataFileConfiguration(
            this.id,
            path
        )
    )

    fun build(): List<DataFileConfiguration> {
        if (files.isEmpty()) {
            throw ConfigException("At least one file should be defined in loader files section")
        }
        validateUniqueID()
        return files.toList()
    }

    private fun validateUniqueID() {
        val dupIDs = files.groupBy { it.id }
        if (dupIDs.any { it.value.size > 1 }) {
            throw ConfigException(
                "File ids<${dupIDs.filter { it.value.size > 1 }.keys.joinToString(
                    ", "
                )}> are duplicated"
            )
        }
    }

    data class FileID(val id: String)
    data class FilePath(val path: String)
}
