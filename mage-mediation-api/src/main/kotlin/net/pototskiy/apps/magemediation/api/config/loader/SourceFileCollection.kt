package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import java.io.File

data class SourceFileCollection(private val files: List<SourceFileDefinition>) : List<SourceFileDefinition> by files {
    @ConfigDsl
    class Builder {
        private val files = mutableListOf<SourceFileDefinition>()

        @Suppress("unused")
        fun Builder.file(id: String, block: PathBuilder.() -> Unit) {
            val sourceFile = SourceFileDefinition(id, PathBuilder().apply(block).build())
            files.add(sourceFile)
            Config.Builder.definedSourceFiles.register(sourceFile)
        }

        fun build() = SourceFileCollection(files)

        class PathBuilder {
            private var path: String? = null

            @Suppress("unused")
            fun PathBuilder.path(path: String) {
                this.path = path
            }

            fun build(): File {
                return File(path ?: throw ConfigException("File path must be defined"))
            }
        }
    }

}
