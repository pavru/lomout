package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppEntityTypeException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.EntityTypeCollection

/**
 * Loader configuration class and builder
 *
 * @property files SourceFileCollection
 * @property entities EntityTypeCollection
 * @property loads LoadCollection
 * @constructor
 */
data class LoaderConfiguration(
    val files: SourceFileCollection,
    val entities: EntityTypeCollection,
    val loads: LoadCollection
) {
    /**
     * Loader configuration builder class
     *
     * @property helper ConfigBuildHelper
     * @property files SourceFileCollection?
     * @property entities EntityTypeCollection?
     * @property loads MutableList<Load>
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var files: SourceFileCollection? = null
        private var entities: EntityTypeCollection? = null
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
         * * [file][SourceFileCollection.Builder.file] - define file id, **mandatory**
         * * [path][SourceFileCollection.Builder.PathBuilder.path] - define file path, **mandatory**
         * * [locale][SourceFileCollection.Builder.PathBuilder.locale] - define file locale, optional
         *
         * @see SourceFileCollection
         *
         * @param block SourceFileCollection.Builder.() -> Unit
         */
        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        /**
         * Define entities that is loaded by loader
         *
         * ```
         * ...
         *  entities {
         *      entity("type name", isOpen:Boolean) {...}
         *      entity("type name", isOpen:Boolean) {...}
         *      ...
         *  }
         * ...
         * ```
         * * [entity][EntityTypeCollection.Builder.entity] - entity definition, **at least one must be defined**
         *
         * @see EntityTypeCollection
         *
         * @param block EntityTypeCollection.Builder.() -> Unit
         */
        fun entities(block: EntityTypeCollection.Builder.() -> Unit) {
            this.entities = EntityTypeCollection.Builder(helper).apply(block).build()
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
         * * fromSources - sources collection to load data from, **at least one source must be defined**
         * * rowsToSkip - number of rows (including header row) to skip before start loading, *optional*
         * * keepAbsentForDays - how long to keep entities that are absent in source data, entities will be
         *      marked as removed
         * * sourceFields - define source data fields
         *
         * @param entityType The entity type name
         * @param block Load.Builder.() -> Unit
         */
        fun loadEntity(entityType: String, block: Load.Builder.() -> Unit) {
            val entity = helper.typeManager.getEntityType(entityType)
                ?: throw AppEntityTypeException("Define entity<$entityType> before load configuration")
            loads.add(Load.Builder(helper, entity).apply(block).build())
        }

        /**
         * Build loader configuration
         *
         * @return LoaderConfiguration
         */
        fun build(): LoaderConfiguration {
            val files = this.files ?: throw AppConfigException("Files is not defined in loader section")
            return LoaderConfiguration(
                files,
                entities ?: throw AppEntityTypeException("At least one entity must be defined"),
                LoadCollection(loads)
            )
        }
    }
}
