package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Loader configuration class and builder
 *
 * @property files SourceFileCollection
 * @property loads LoadCollection
 * @constructor
 */
data class LoaderConfiguration(
    val files: SourceFileCollection,
    val loads: LoadCollection
) {
    /**
     * Loader configuration builder class
     *
     * @property helper ConfigBuildHelper
     * @property files SourceFileCollection?
     * @property loads MutableList<Load>
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var files: SourceFileCollection? = null
        private var loads = mutableListOf<Load>()

        /**
         * Source file configuration
         *
         * ```
         * ...
         *  files {
         *      file("file id") { path("file path"); locale("cc_LL") }
         *      file("file id") {
         *          path("file path")
         *          locale("cc_LL")
         *      }
         *      ...
         *  }
         * ...
         * ```
         * * [file][SourceFileCollection.Builder.file] — define file id, **mandatory**
         * * [path][SourceFileCollection.Builder.PathBuilder.path] — define file path, **mandatory**
         * * [locale][SourceFileCollection.Builder.PathBuilder.locale] — define file locale, optional
         *
         * @see SourceFileCollection
         *
         * @param block Files definition
         */
        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        /**
         * Entity load entity configuration
         *
         * ```
         * ...
         *  loadEntity("type name") {
         *      fromSources {
         *          source { file(...); sheet(...); stopOnEmptyRow() }
         *          source { file(...); sheet(...); stopOnEmptyRow() }
         *          ...
         *      }
         *      rowsToSkip(number of rows)
         *      keepAbsentForDays(days)
         *      sourceFields {
         *          main("name") {...}
         *          extra("name") {...}
         *      }
         *  }
         * ...
         * ```
         * * fromSources — sources collection to load data from, **at least one source must be defined**
         * * rowsToSkip — number of rows (including header row) to skip before start loading, *optional*
         * * keepAbsentForDays — how long to keep entities that are absent in source data, entities will be
         *      marked as removed
         * * sourceFields — define source data fields
         *
         * @param entityType The entity type name
         * @param block The entity loading instruction
         */
        fun loadEntity(entityType: KClass<out Document>, block: Load.Builder.() -> Unit) {
            loads.add(Load.Builder(helper, entityType).apply(block).build())
        }

        /**
         * Build loader configuration
         *
         * @return LoaderConfiguration
         */
        fun build(): LoaderConfiguration {
            val files =
                this.files ?: throw AppConfigException(
                    unknownPlace(),
                    message("message.error.config.load.file.not_defined")
                )
            return LoaderConfiguration(
                files,
                LoadCollection(loads)
            )
        }
    }
}
