package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.EntityType

/**
 * Field set configuration
 *
 * @property sets List<FieldSet> The list of field sets
 * @property mainSet FieldSet The main field set
 * @constructor
 */
data class FieldSetCollection(private val sets: List<FieldSet>) : List<FieldSet> by sets {
    val mainSet: FieldSet
        get() = this.find { it.mainSet }!!

    /**
     * Field sets builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property entityType EntityType The entity type
     * @property withSourceHeaders Boolean
     * @property sources SourceDataCollection? Sources to load data
     * @property headerRow Int? The header row number, zero based
     * @property fieldSets MutableList<FieldSet> List of fields
     * @constructor
     */
    @ConfigDsl
    class Builder(
        private val helper: ConfigBuildHelper,
        private val entityType: EntityType,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?
    ) {
        private val fieldSets = mutableListOf<FieldSet>()

        /**
         * Define main field set, **field set can have only one main set**
         *
         * ```
         * ...
         *  main("name") {
         *      field("name") {...} to attribute("name")
         *      field("name") {...} to attribute("name")
         *      field("name") {...}
         *      ...
         *  }
         * ...
         * ```
         *
         * @param name The field set name
         * @param block The main set definition
         * @return Boolean
         */
        @ConfigDsl
        fun main(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    helper,
                    entityType,
                    name,
                    true,
                    withSourceHeaders,
                    sources,
                    headerRow
                ).apply(block).build()
            )

        /**
         * Define extra field set, *several extra sets can be defined*
         *
         * ```
         * ...
         *  extra("name") {
         *      field("name") {...} to attribute("name")
         *      field("name") {...} to attribute("name")
         *      field("name") {...}
         *      ...
         *  }
         * ...
         * ```
         *
         * @param name The field set
         * @param block The extra field set definition
         * @return Boolean
         */
        @ConfigDsl
        fun extra(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    helper,
                    entityType,
                    name,
                    false,
                    withSourceHeaders,
                    sources,
                    headerRow
                ).apply(block).build()
            )

        /**
         * Build field sets collection
         *
         * @return FieldSetCollection
         */
        fun build(): FieldSetCollection {
            if (!fieldSets.any { it.mainSet }) {
                throw AppConfigException("Field set collection must contain main set")
            }
            return FieldSetCollection(fieldSets)
        }
    }
}
