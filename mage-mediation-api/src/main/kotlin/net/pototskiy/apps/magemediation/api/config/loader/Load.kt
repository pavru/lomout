package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Entity

data class Load(
    val headersRow: Int,
    val rowsToSkip: Int,
    val maxAbsentDays: Int,
    val entity: Entity,
    val sources: SourceDataCollection,
    val fieldSets: FieldSetCollection
) {
    @ConfigDsl
    class Builder(private val entity: Entity) {
        private var headersRow: Int? = null
        private var rowsToSkip: Int = 0
        private var maxAbsentDays: Int = 5
        private var sources: SourceDataCollection? = null
        private var fieldSets: FieldSetCollection? = null

        @Suppress("unused")
        fun Builder.headersRow(row: Int) {
            this.headersRow = row
        }

        @Suppress("unused")
        fun Builder.rowsToSkip(rows: Int) {
            this.rowsToSkip = rows
        }

        @Suppress("unused")
        fun Builder.keepAbsentForDays(days: Int) {
            this.maxAbsentDays = days
        }

        @Suppress("unused")
        fun Builder.fromSources(block: SourceDataCollection.Builder.() -> Unit) {
            this.sources = SourceDataCollection.Builder().apply(block).build()
        }

        @Suppress("unused")
        fun Builder.sourceFields(block: FieldSetCollection.Builder.() -> Unit) {
            val attributes = entity.attributes
            fieldSets = FieldSetCollection.Builder(attributes, headersRow != UNDEFINED_COLUMN).apply(block).build()
        }


        fun build(): Load {
            val headersRow = this.headersRow ?: UNDEFINED_COLUMN
            val rowsToSkip = this.rowsToSkip
            val sources =
                this.sources ?: throw ConfigException("Source files are not defined for entity<${entity.name}> loading")
            validateFieldColumnDefinition()
            return Load(
                headersRow,
                rowsToSkip,
                maxAbsentDays,
                entity,
                sources,
                fieldSets ?: throw ConfigException("Field set is not defined for entity<${entity.name}> loading")
            )
        }

        private fun validateFieldColumnDefinition() {
            var fields =
                (fieldSets ?: throw ConfigException("Field set is not defined for entity<${entity.name}> loading"))
                    .map { it.fields.toList() }
                    .flatten()
                    .toMap()
            fields = fields
                .filterNot { it.key.isNested }
                .filterNot { it.value.isSynthetic }
            if (this.headersRow == null && fields.any { it.key.column == UNDEFINED_COLUMN }) {
                throw ConfigException(
                    "Dataset has no headers row but " +
                            "fields<${fields.filter { it.key.column == UNDEFINED_COLUMN }.map { it.value.name }
                                .joinToString(", ")}> " +
                            "has no column defined"
                )
            }
        }
    }
}
