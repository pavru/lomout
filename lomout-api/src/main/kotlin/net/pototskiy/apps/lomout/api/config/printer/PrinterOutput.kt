package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.UNDEFINED_ROW
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection
import net.pototskiy.apps.lomout.api.config.loader.SourceData
import net.pototskiy.apps.lomout.api.config.loader.SourceDataCollection
import net.pototskiy.apps.lomout.api.entity.EntityType

/**
 * Printer line output definition
 *
 * @property file SourceData The file to output printer line result
 * @property printHead Boolean The flag to print headers in first row
 * @property fieldSets FieldSetCollection The fields to print
 * @constructor
 */
data class PrinterOutput(
    val file: SourceData,
    val printHead: Boolean,
    val fieldSets: FieldSetCollection
) {
    /**
     * Printer output builder class
     *
     * @property helper ConfigBuildHelper The configuration builder helper
     * @property entityType EntityType The target entity type
     * @property printHead Boolean The flag to print or not headers
     * @property file SourceData? The output file
     * @property fieldSets FieldSetCollection? The fields to print
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper, private val entityType: EntityType) {
        /**
         * Print or not headers, default: true (print)
         */
        @ConfigDsl
        var printHead: Boolean = true
        private var file: SourceData? = null
        private var fieldSets: FieldSetCollection? = null

        /**
         * The output file
         *
         * ```
         * ...
         *  file { file("file id"); sheet("sheet name") }
         * ...
         * ```
         * * file - reference to file define in files block, **mandatory**
         * * sheet - sheet name to print result data, **mandatory**
         *
         * @param block SourceData.Builder.() -> Unit
         */
        @ConfigDsl
        fun file(block: SourceData.Builder.() -> Unit) {
            this.file = SourceData.Builder(helper).apply(block).build()
            if (this.file?.sheet?.name == null) {
                throw AppConfigException("Sheet name, not regex must be used in output")
            }
        }

        /**
         * Define output field set. Main set is printed for each entity, extra field set is printed only when
         * data changed.
         *
         * ```
         * ...
         *  outputFields {
         *      main("set name") {
         *          field("field name")
         *          field("field name") to attribute(...)
         *          ...
         *      }
         *      extra("set name") {
         *          field("field name") to attribute(...)
         *          field("field name")
         *          ...
         *      }
         *  }
         * ...
         * ```
         * * [main][net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection.Builder.main] - main field set
         *      for output, **mandatory, only one main set is allowed**
         * * [extra][net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection.Builder.extra] - extra field
         *      set, *optional, 0 or several extra set are allowed*
         *
         * @param block FieldSetCollection.Builder.() -> Unit
         */
        @ConfigDsl
        fun outputFields(block: FieldSetCollection.Builder.() -> Unit) {
            this.fieldSets = FieldSetCollection.Builder(
                helper,
                entityType,
                false,
                SourceDataCollection(emptyList()),
                UNDEFINED_ROW
            ).apply(block).build()
        }

        /**
         * Build printer output configuration
         *
         * @return PrinterOutput
         */
        fun build(): PrinterOutput {
            return PrinterOutput(
                file ?: throw AppConfigException("Output file must be defined"),
                printHead,
                fieldSets ?: throw AppConfigException("Field sets must be defined")
            )
        }
    }
}
