package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.UNDEFINED_COLUMN
import net.pototskiy.apps.lomout.api.UNDEFINED_ROW
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Define entity load configuration
 *
 * @property headersRow The header row number
 * @property rowsToSkip The number of rows before start loading
 * @property maxAbsentDays The days to store entity that is removed from source data
 * @property entity The entity type
 * @property sources Source for loading
 * @property fieldSets Field sets of sources
 * @constructor
 */
data class Load(
    val headersRow: Int,
    val rowsToSkip: Int,
    val maxAbsentDays: Int,
    val entity: KClass<out Document>,
    val sources: SourceDataCollection,
    val fieldSets: FieldSetCollection
) {
    /**
     * Entity loading configuration builder class
     *
     * @property helper The config build helper
     * @property entityType The entity type
     * @property headersRow The number of header row
     * @property rowsToSkip The number of rows to skip before start loading
     * @property maxAbsentDays The day to keep removed entities
     * @property sources The loading sources
     * @property fieldSets Field sets of source
     * @constructor
     */
    @ConfigDsl
    class Builder(
        private val helper: ConfigBuildHelper,
        private val entityType: KClass<out Document>
    ) {
        private var headersRow: Int = UNDEFINED_ROW
        private var rowsToSkip: Int = 0
        private var maxAbsentDays: Int = defaultAbsentDays
        private var sources: SourceDataCollection? = null
        private var fieldSets: FieldSetCollection? = null

        /**
         * Configure header row number, *optional, default: no header*
         *
         * @param row The row number, zero based
         */
        fun headersRow(row: Int) {
            headersRow = row
        }

        /**
         * Configure number of rows to skip, *optional, default: 0*, if source has heard row this row will be skipped
         *
         * @param rows The number of rows (including header row)
         */
        fun rowsToSkip(rows: Int) {
            this.rowsToSkip = rows
        }

        /**
         * Configure how long to keep removed entity in the DB
         *
         * @param days Days to keep
         */
        fun keepAbsentForDays(days: Int) {
            this.maxAbsentDays = days
        }

        /**
         * Configure sources to load entities
         *
         * ```
         * ...
         *  fromSources {
         *      source { file(...); sheet(...) }
         *      source { file(...); sheet(...) }
         *      ...
         *  }
         * ...
         * ```
         * * source — **at least one source must be defined**
         *
         * @param block Sources configuration
         */
        fun fromSources(block: SourceDataCollection.Builder.() -> Unit) {
            this.sources = SourceDataCollection.Builder(helper).apply(block).build()
        }

        /**
         * Define source field sets
         *
         * ```
         * ...
         *  sourceFields {
         *      main("name") {...}
         *      extra("name) {...}
         *      extra("name) {...}
         *      ...
         *  }
         * ...
         * ```
         * * main — define main field set, **mandatory**, only one is allowed
         * * extra — define extra field set, *optional*
         *
         * @param block Source fields definition
         */
        fun sourceFields(block: FieldSetCollection.Builder.() -> Unit) {
            fieldSets = FieldSetCollection.Builder(
                helper,
                entityType,
                headersRow != UNDEFINED_COLUMN,
                sources,
                headersRow,
                true
            ).apply(block).build()
        }

        /**
         * Build entity loading configuration
         *
         * @return Load
         */
        fun build(): Load {
            val headersRow = this.headersRow
            val rowsToSkip = this.rowsToSkip
            val sources =
                this.sources
                    ?: throw AppConfigException(
                        unknownPlace(),
                        "Source files are not defined for entity type '${entityType.qualifiedName}' loading."
                    )
            validateFieldColumnDefinition()
            return Load(
                headersRow,
                rowsToSkip,
                maxAbsentDays,
                entityType,
                sources,
                fieldSets
                    ?: throw AppConfigException(
                        unknownPlace(),
                        "Field set is not defined for entity type '${entityType.qualifiedName}' loading."
                    )
            )
        }

        private fun validateFieldColumnDefinition() {
            val fields =
                (fieldSets
                    ?: throw AppConfigException(
                        unknownPlace(),
                        "Field set is not defined for entity type '${entityType.qualifiedName}' loading."
                    ))
                    .map { it.fieldToAttr.toList() }
                    .flatten()
                    .toMap()
            if (this.headersRow == UNDEFINED_ROW && fields.any { it.key.column == UNDEFINED_COLUMN }) {
                throw AppConfigException(
                    unknownPlace(),
                    "Dataset has no headers row but " +
                            "fields '${fields.filter { it.key.column == UNDEFINED_COLUMN }.map { it.value.name }
                                .joinToString(", ")}' " +
                            "has no column defined."
                )
            }
        }

        /**
         * Companion object
         */
        companion object {
            /**
             * Default days to keep
             */
            const val defaultAbsentDays = 5
        }
    }
}
