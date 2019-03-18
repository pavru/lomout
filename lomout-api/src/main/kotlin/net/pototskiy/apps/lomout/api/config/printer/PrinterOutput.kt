package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.UNDEFINED_ROW
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.loader.FieldSetCollection
import net.pototskiy.apps.lomout.api.config.loader.SourceData
import net.pototskiy.apps.lomout.api.config.loader.SourceDataCollection
import net.pototskiy.apps.lomout.api.entity.EntityType

data class PrinterOutput(
    val file: SourceData,
    val printHead: Boolean,
    val fieldSets: FieldSetCollection
) {
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper, private val entityType: EntityType) {
        @ConfigDsl
        var printHead: Boolean = true
        private var file: SourceData? = null
        private var fieldSets: FieldSetCollection? = null

        @ConfigDsl
        fun file(block: SourceData.Builder.() -> Unit) {
            this.file = SourceData.Builder(helper).apply(block).build()
            if (this.file?.sheet?.name == null) {
                throw AppConfigException("Sheet name, not regex must be used in output")
            }
        }

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

        fun build(): PrinterOutput {
            return PrinterOutput(
                file ?: throw AppConfigException("Output file must be defined"),
                printHead,
                fieldSets ?: throw AppConfigException("Field sets must be defined")
            )
        }
    }
}
